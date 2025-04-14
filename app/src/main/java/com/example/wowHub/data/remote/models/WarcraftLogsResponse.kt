package com.example.wowHub.data.remote.models

import com.example.wowHub.data.local.db.entities.Report

data class WarcraftLogsResponse(
    val data: ReportData
)

data class ReportData(
    val reportData: ReportContainer
)

data class ReportContainer(
    val reports: ReportList
)

data class ReportList(
    val data: List<Report>
)

data class Report(
    val code: String,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val zone: Zone
)

data class Zone(
    val name: String
)
