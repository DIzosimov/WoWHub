package com.example.wowHub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wowHub.viewmodel.GuildViewModel
import com.example.wowHub.data.local.db.entities.Report

@Composable
fun WarcraftLogsScreen(viewModel: GuildViewModel) {
    val reports by viewModel.reports.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(reports) { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = report.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Owner: ${report.owner}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Start: ${report.start}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "End: ${report.end}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}