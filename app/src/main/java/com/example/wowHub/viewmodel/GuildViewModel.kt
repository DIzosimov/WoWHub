package com.example.wowHub.viewmodel

import com.example.wowHub.BuildConfig

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wowHub.data.local.db.GuildDao
import com.example.wowHub.data.local.db.entities.Report
import com.example.wowHub.data.local.db.entities.WoWAuditMember
import com.example.wowHub.data.remote.api.WarcraftLogsApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import com.example.wowHub.data.remote.models.WoWAuditResponse
import kotlinx.coroutines.launch

// --- Repository Layer ---

class GuildRepository(
    private val dao: GuildDao,
    private val warcraftLogsApi: WarcraftLogsApi,
    private val wowAuditApi: WoWAuditApi
) {
    suspend fun refreshReports(guild: String, server: String, region: String, apiKey: String) {
        val reports = warcraftLogsApi.getReports(guild, server, region, apiKey)
        dao.insertReports(reports)
    }

    suspend fun refreshWoWAuditRoster() {
        try {
            Log.d("GuildRepository", "Attempting to fetch WoWAudit roster...")
            val authHeader = "Bearer ${BuildConfig.WOWAUDIT_API_KEY}"
            val response = wowAuditApi.getRoster(authHeader)
            Log.d("GuildRepository", "Raw API response: $response")
            
            val members = response.mapNotNull { response ->
                try {
                    Log.d("GuildRepository", "Processing member: id=${response.id}, name=${response.name}, class=${response.wowClass}, role=${response.role}, attendance=${response.attendance}")
                    WoWAuditMember(
                        id = response.id ?: "unknown_${System.currentTimeMillis()}",
                        characterName = response.name ?: "Unknown",
                        characterClass = response.wowClass ?: "Unknown",
                        characterRole = response.role ?: "Unknown",
                        attendance = response.attendance
                    )
                } catch (e: Exception) {
                    Log.e("GuildRepository", "Error creating member from response: $response", e)
                    null
                }
            }
            
            if (members.isNotEmpty()) {
                dao.insertWoWAuditMembers(members)
                Log.d("GuildRepository", "Inserted ${members.size} members into database")
            } else {
                Log.w("GuildRepository", "No valid members to insert into database")
            }
        } catch (e: Exception) {
            Log.e("GuildRepository", "Error fetching WoWAudit data", e)
        }
    }

    suspend fun getStoredReports(): List<Report> = dao.getAllReports()
    suspend fun getStoredWoWAuditMembers(): List<WoWAuditMember> = dao.getAllWoWAuditMembers()
}

// --- ViewModel Layer ---

class GuildViewModel(private val repository: GuildRepository) : ViewModel() {
    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> get() = _reports

    private val _auditRoster = MutableLiveData<List<WoWAuditMember>>()
    val auditRoster: LiveData<List<WoWAuditMember>> get() = _auditRoster

    fun loadReports(guild: String, server: String, region: String, apiKey: String) {
        viewModelScope.launch {
            repository.refreshReports(guild, server, region, apiKey)
            _reports.value = repository.getStoredReports()
        }
    }

    fun loadWoWAuditRoster() {
        viewModelScope.launch {
            Log.d("GuildViewModel", "Loading WoWAudit roster...")
            repository.refreshWoWAuditRoster()
            val members = repository.getStoredWoWAuditMembers()
            Log.d("GuildViewModel", "Retrieved ${members.size} members from database")
            _auditRoster.value = members
        }
    }
}