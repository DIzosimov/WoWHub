package com.example.wowHub.viewmodel

import com.example.wowHub.BuildConfig

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wowHub.data.local.db.GuildDao
import com.example.wowHub.data.local.db.entities.GuildMember
import com.example.wowHub.data.local.db.entities.Report
import com.example.wowHub.data.local.db.entities.WoWAuditMember
import com.example.wowHub.data.remote.GraphQL.GraphQLRequest
import com.example.wowHub.data.remote.api.WarcraftLogsApi
import com.example.wowHub.data.remote.api.WarcraftLogsGraphQLApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import com.example.wowHub.data.remote.models.CharacterDetails
import kotlinx.coroutines.launch

// --- Repository Layer ---

class GuildRepository(
    private val dao: GuildDao,
    private val wowAuditApi: WoWAuditApi,
    private val warcraftLogsGraphQLApi: WarcraftLogsGraphQLApi

) {
    suspend fun refreshReports(authToken: String) {
        val endTime = System.currentTimeMillis()
        try {
            val query = """
{
  reportData {
    reports(guildName: "crystal method", guildServerSlug: "tarren-mill", guildServerRegion: "EU") {
      data {
        code
        title
        startTime
        endTime
      }
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
                )
            }

            dao.insertReports(reportEntities)
        } catch (e: Exception) {
            Log.e("GuildRepository", "Error fetching reports from Warcraft Logs V2 API", e)
        }
    }

    private fun mapCharacterToGuildMember(existingId: String, character: CharacterDetails): GuildMember {
        return GuildMember(
            id = existingId,
            name = character.name,
            classID = character.classID,
            level = character.level,
            faction = character.faction,
            guildRank = character.guildRank,
            serverName = character.server.name,
            serverSlug = character.server.slug
        )
    }


    suspend fun refreshWoWAuditRoster() {
        try {
            Log.d("GuildRepository", "Attempting to fetch WoWAudit roster...")
            val authHeader = "Bearer ${BuildConfig.WOWAUDIT_API_KEY}"
            val response = wowAuditApi.getRoster(authHeader)
            Log.d("GuildRepository", "Raw API response: $response")

            val members = response.mapNotNull { response ->
                try {
                    Log.d("GuildRepository", "Processing member: id=${response.id}, name=${response.name}, class=${response.wowClass}, role=${response.role}, attendance=${response.attendance}, realm=${response.realm}")
                    WoWAuditMember(
                        id = response.id ?: "unknown_${System.currentTimeMillis()}",
                        characterName = response.name ?: "Unknown",
                        characterClass = response.wowClass ?: "Unknown",
                        characterRole = response.role ?: "Unknown",
                        attendance = response.attendance,
                        realm = response.realm ?: "Unknown"
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
    suspend fun getStoredGuildMembers(): List<GuildMember> = dao.getAllMembers()

    suspend fun populateGuildMemberData(authToken: String) {
        val members = getStoredWoWAuditMembers()
        val guildMembers = mutableListOf<GuildMember>()

        for (member in members) {
            val query = """
            {
              characterData {
                character(name: "${member.characterName}", serverSlug: "${member.realm}", serverRegion: "EU") {
                  id
                  canonicalID
                  name
                  classID
                  level
                  guildRank
                  server {
                    name
                    slug
                  }
                }
              }
            }
        """.trimIndent()

            try {
                val response = warcraftLogsGraphQLApi.getCharacter(
                    auth = "Bearer $authToken",
                    body = GraphQLRequest(query)
                )

                val character = response.data?.characterData?.character

                if (character != null) {
                    val enriched = mapCharacterToGuildMember(member.id, character)
                    guildMembers.add(enriched)
                }
            } catch (e: Exception) {
                Log.e("CharacterSync", "Error fetching data for ${member.characterName}", e)
            }
        }

        if (guildMembers.isNotEmpty()) {
            for (member in guildMembers) {
                dao.insertMember(member)
            }
            Log.d("CharacterSync", "Updated ${guildMembers.size} guild members")
        }
    }

    suspend fun refreshAllReports(authToken: String) {
        var endTime = System.currentTimeMillis()
        val allReports = mutableListOf<Report>()

        try {
            while (true) {
                val query = """
                {
                  reportData {
                    reports(
                      guildName: "crystal method",
                      guildServerSlug: "tarren-mill",
                      guildServerRegion: "EU",
                      limit: 10,
                      endTime: $endTime
                    ) {
                      data {
                        code
                        title
                        startTime
                        endTime
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

                val reports = response.data
                    .reportData
                    .reports
                    .data
                    ?: emptyList()

                allReports.addAll(
                    reports.map {
                        Report(
                            code = it.code,
                            title = it.title,
                            startTime = it.startTime,
                            endTime = it.endTime
                        )
                    }
                )

                val nextPage = response.data.reportData.reports.nextPageTimestamp
                if (nextPage == null) break else endTime = nextPage
            }

            dao.insertReports(allReports)
            Log.d("GuildRepository", "Fetched total ${allReports.size} reports")

        } catch (e: Exception) {
            Log.e("GuildRepository", "Error fetching paginated reports", e)
        }
    }


    // --- ViewModel Layer ---

    class GuildViewModel(private val repository: GuildRepository) : ViewModel() {
        private val _reports = MutableLiveData<List<Report>>()
        val reports: LiveData<List<Report>> get() = _reports

        private val _auditRoster = MutableLiveData<List<WoWAuditMember>>()
        val auditRoster: LiveData<List<WoWAuditMember>> get() = _auditRoster

        private val _guildRoster = MutableLiveData<List<GuildMember>>()
        val guildRoster: LiveData<List<GuildMember>> get() = _guildRoster

        fun loadReports(authToken: String) {
            viewModelScope.launch {
                repository.refreshReports(authToken)
                _reports.value = repository.getStoredReports()
            }
        }

        fun loadGuildMembers(authToken: String) {
            viewModelScope.launch {
                Log.d("GuildViewModel", "Loading WarcraftLogsRoster...")
                repository.populateGuildMemberData(authToken)
                val guildMembers = repository.getStoredGuildMembers()
                Log.d("GuildViewModel", "Retrieved ${guildMembers.size} members from database")
                _guildRoster.value = repository.getStoredGuildMembers()
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

        fun loadAllReports(authToken: String) {
            viewModelScope.launch {
                repository.refreshAllReports(authToken)
                _reports.value = repository.getStoredReports()
            }
        }
    }
}