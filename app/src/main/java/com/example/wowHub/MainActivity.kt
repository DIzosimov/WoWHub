package com.example.wowHub

import com.example.wowHub.BuildConfig
import com.example.wowHub.data.remote.api.WarcraftLogsAuthApi
import com.example.wowHub.ui.navigation.Screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.wowHub.data.local.db.GuildDatabase
import com.example.wowHub.data.remote.api.WarcraftLogsGraphQLApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import com.example.wowHub.ui.screens.WoWAuditRosterScreen
import com.example.wowHub.ui.theme.WowHubTheme
import com.example.wowHub.viewmodel.GuildRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.wowHub.utils.TokenManager
import com.example.wowHub.ui.navigation.NavGraph

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            GuildDatabase::class.java,
            "guild_database"
        )
            .fallbackToDestructiveMigration(true) //Wipes out DB on schema change only use in DEV
            .build()

        val wowAuditRetrofit = Retrofit.Builder()
            .baseUrl("https://wowaudit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val warcraftLogsGraphQLApi = Retrofit.Builder()
            .baseUrl("https://www.warcraftlogs.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WarcraftLogsGraphQLApi::class.java)

        val warcraftLogsAuthApi = Retrofit.Builder()
            .baseUrl("https://www.warcraftlogs.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WarcraftLogsAuthApi::class.java)

        val tokenManager = TokenManager(
            authApi = warcraftLogsAuthApi,
            clientId = BuildConfig.WARCRAFTLOGS_CLIENT_KEY,
            clientSecret = BuildConfig.WARCRAFTLOGS_SECRET_KEY
        )

        val wowAuditApi = wowAuditRetrofit.create(WoWAuditApi::class.java)

        val repository = GuildRepository(
            dao = db.guildDao(),
            wowAuditApi = wowAuditApi,
            warcraftLogsGraphQLApi = warcraftLogsGraphQLApi
        )

        val viewModel = GuildRepository.GuildViewModel(repository)

        setContent {
            WowHubTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LaunchedEffect(Unit) {
                        viewModel.loadWoWAuditRoster()
                        val token = tokenManager.getValidToken()
                        viewModel.loadReports(token)
                        viewModel.loadGuildMembers(token)
                    }

                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}