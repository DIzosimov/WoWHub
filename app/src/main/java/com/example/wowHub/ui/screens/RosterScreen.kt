package com.example.wowHub.ui.screens

import com.example.wowHub.viewmodel.GuildViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*



@Composable
fun WoWAuditRosterScreen(viewModel: GuildViewModel) {
    val roster by viewModel.auditRoster.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(roster) { member ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = member.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Class: ${member.className}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Role: ${member.role}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Attendance: ${member.attendance ?: 0.0}%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}