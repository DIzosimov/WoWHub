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
import com.example.wowHub.data.remote.GraphQL.GraphQLRequest
import com.example.wowHub.data.remote.api.WarcraftLogsApi
import com.example.wowHub.data.remote.api.WarcraftLogsGraphQLApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import kotlinx.coroutines.launch

// --- Repository Layer ---

class GuildRepository(
    private val dao: GuildDao,
    private val wowAuditApi: WoWAuditApi,
    private val warcraftLogsGraphQLApi: WarcraftLogsGraphQLApi

) {
    suspend fun refreshReports(authToken: String) {
        try {
            val query = """
{
  reportData {
    reports(
      guildName: "Crystal Method",
      guildServerSlug: "tarren-mill",
      guildServerRegion: "EU",
      limit: 10,
      endTime: System.currentTimeMillis()
    ) {
      data {
        code
        title
        startTime
        endTime
        zone {
          name
        }
      }
      nextPageTimestamp
    }
  }
}
""".trimIndent()


            val response = warcraftLogsGraphQLApi.getReports(
                auth = "Bearer $authToken",
                body = GraphQLRequest(query)
            )

            Log.d("WarcraftLogs", "Full response: $response")

            val reportEntities = response.data.reportData.reports.data.map {
                Report(
                    code = it.code,
                    title = it.title,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    zoneName = it.zoneName
                )
            }

            dao.insertReports(reportEntities)
        } catch (e: Exception) {
            Log.e("GuildRepository", "Error fetching reports from Warcraft Logs V2 API", e)
        }
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

    suspend fun fetchWarcraftLogsReports(authToken: String): List<Report> {
        val query = """
{
  reportData {
    guild(name: "Crystal-Method", serverSlug: "tarren-mill", serverRegion: "EU") {
      reports(limit: 5) {
        data {
          code
          title
          startTime
          endTime
          visibility
          zone {
            name
          }
          fights {
            id
            name
            startTime
            endTime
            kill
            difficulty
          }
        }
      }
    }
  }
}
""".trimIndent()


        val response = warcraftLogsGraphQLApi.getReports(
            auth = "Bearer $authToken",
            body = GraphQLRequest(query)
        )

        return response.data.reportData.reports.data
    }

    // --- ViewModel Layer ---

    class GuildViewModel(private val repository: GuildRepository) : ViewModel() {
        private val _reports = MutableLiveData<List<Report>>()
        val reports: LiveData<List<Report>> get() = _reports

        private val _auditRoster = MutableLiveData<List<WoWAuditMember>>()
        val auditRoster: LiveData<List<WoWAuditMember>> get() = _auditRoster

        fun loadReports(authToken: String) {
            viewModelScope.launch {
                repository.refreshReports(authToken)
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
}