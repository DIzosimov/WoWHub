package com.example.wowHub

import com.example.wowHub.BuildConfig
import com.example.wowHub.data.remote.api.WarcraftLogsAuthApi
import com.example.wowHub.ui.navigation.Screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.wowHub.data.local.db.GuildDatabase
import com.example.wowHub.data.remote.api.WarcraftLogsGraphQLApi
import com.example.wowHub.data.remote.api.WoWAuditApi
import com.example.wowHub.ui.theme.WowHubTheme
import com.example.wowHub.viewmodel.GuildRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.wowHub.utils.TokenManager
import com.example.wowHub.ui.navigation.NavGraph
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "WowHub",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Divider()
                            NavigationDrawerItem(
                                label = { Text("Home") },
                                selected = navController.currentDestination?.route == Screen.Home.route,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Roster") },
                                selected = navController.currentDestination?.route == Screen.Roster.route,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Screen.Roster.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("WowHub") },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        LaunchedEffect(Unit) {
                            viewModel.loadWoWAuditRoster()
                            val token = tokenManager.getValidToken()
                            viewModel.loadReports(token)
                            viewModel.loadGuildMembers(token)
                        }

                        NavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}