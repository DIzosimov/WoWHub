package com.example.wowHub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wowHub.data.local.db.GuildDao
import com.example.wowHub.data.local.db.entities.Report
import com.example.wowHub.data.local.db.entities.WoWAuditMember
import com.example.wowHub.data.remote.api.WarcraftLogsApi
import com.example.wowHub.data.remote.api.WoWAuditApi
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

    suspend fun refreshWoWAuditRoster(region: String, realm: String, guild: String) {
        val auditData = wowAuditApi.getRoster(region, realm, guild)
        dao.insertWoWAuditMembers(auditData.roster)
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

    fun loadWoWAuditRoster(region: String, realm: String, guild: String) {
        viewModelScope.launch {
            repository.refreshWoWAuditRoster(region, realm, guild)
            _auditRoster.value = repository.getStoredWoWAuditMembers()
        }
    }
}