package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey val code: String,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val zoneName: String
)