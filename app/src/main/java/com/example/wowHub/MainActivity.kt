package com.example.wowHub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.wowHub.data.local.db.GuildDatabase
import com.example.wowHub.data.remote.api.WarcraftLogsApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import com.example.wowHub.ui.screens.WoWAuditRosterScreen
import com.example.wowHub.ui.theme.WowHubTheme
import com.example.wowHub.viewmodel.GuildRepository
import com.example.wowHub.viewmodel.GuildViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            GuildDatabase::class.java,
            "guild_database"
        ).build()

        val warcraftLogsRetrofit = Retrofit.Builder()
            .baseUrl("https://www.warcraftlogs.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val wowAuditRetrofit = Retrofit.Builder()
            .baseUrl("https://wowaudit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val warcraftLogsApi = warcraftLogsRetrofit.create(WarcraftLogsApi::class.java)
        val wowAuditApi = wowAuditRetrofit.create(WoWAuditApi::class.java)

        val repository = GuildRepository(
            dao = db.guildDao(),
            warcraftLogsApi = warcraftLogsApi,
            wowAuditApi = wowAuditApi
        )

        val viewModel = GuildViewModel(repository)

        setContent {
            MaterialTheme {
                LaunchedEffect(Unit) {
                    viewModel.loadWoWAuditRoster("eu", "tarren-mill", "crystal-method")
                }
                WoWAuditRosterScreen(viewModel = viewModel)
            }
        }
    }
}