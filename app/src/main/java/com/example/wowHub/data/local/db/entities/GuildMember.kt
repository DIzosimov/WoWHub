package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GuildMember")
data class GuildMember(
    @PrimaryKey val id: String,
    val name: String,
    val classID: Int?,
    val level: Int?,
    val faction: String?,
    val guildRank: Int?,
    val serverName: String?,
    val serverSlug: String?
)
