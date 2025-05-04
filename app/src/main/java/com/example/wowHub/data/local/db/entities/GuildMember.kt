package com.example.wowHub.data.local.db.entities

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

@Entity(tableName = "GuildMember")
data class GuildMember(
    @PrimaryKey val id: String,
    val name: String,
    val classID: Int?,
    val level: Int?,
    val faction: String?,
    val guildRank: Int?,
    val serverName: String?,
    val serverSlug: String?,
    val zoneRankingsJson: String? = null
) {

    fun getZoneRankings(): ZoneRankings? {
        return try {
            zoneRankingsJson?.let { raw ->
                Gson().fromJson(raw, ZoneRankings::class.java)
            }
        } catch (e: JsonSyntaxException) {
            Log.e("ZoneRankings", "JsonSyntaxException while parsing: ${e.localizedMessage}")
            null
        } catch (e: Exception) {
            Log.e("ZoneRankings", "Unexpected error while parsing: ${e.localizedMessage}")
            null
        }
    }
}
