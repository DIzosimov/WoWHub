package com.example.wowHub.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey val id: String,
    val title: String,
    val owner: String,
    val start: Long,
    val end: Long
)