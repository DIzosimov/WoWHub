package com.example.wowHub.data.local.db.entities

data class WoWAuditData(
    val guildName: String,
    val roster: List<WoWAuditMember>
)