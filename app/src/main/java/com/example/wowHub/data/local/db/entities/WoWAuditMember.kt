package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WoWAuditMember(
    @PrimaryKey val id: String, // or use a unique fallback like characterName if no ID
    val characterName: String,
    val characterClass: String,
    val characterRole: String,
    val attendance: Float?
)