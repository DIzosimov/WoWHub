package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WoWAuditMember(
    @PrimaryKey val name: String,
    val className: String,
    val role: String,
    val attendance: Double?
)