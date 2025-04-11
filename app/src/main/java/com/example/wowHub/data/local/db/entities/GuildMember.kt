package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GuildMember(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val notes: String?
)