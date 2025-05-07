package com.example.wowHub.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.wowHub.R
import com.example.wowHub.data.local.db.entities.GuildMember
import com.example.wowHub.viewmodel.GuildRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    characterName: String,
    viewModel: GuildRepository.GuildViewModel,
    onNavigateBack: () -> Unit
) {
    val guildMembers by viewModel.guildRoster.observeAsState(emptyList())
    val character = guildMembers.find { it.name.equals(characterName, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(characterName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            character?.let { guildMember ->
                val classIcon = when (guildMember.classID) {
                    13 -> R.drawable.class_warrior
                    6 -> R.drawable.class_paladin
                    3 -> R.drawable.class_hunter
                    8 -> R.drawable.class_rogue
                    7 -> R.drawable.class_priest
                    1 -> R.drawable.class_death_knight
                    9 -> R.drawable.class_shaman
                    4 -> R.drawable.class_mage
                    10 -> R.drawable.class_warlock
                    5 -> R.drawable.class_monk
                    2 -> R.drawable.class_druid
                    12 -> R.drawable.class_demonhunter
                    11 -> R.drawable.class_evoker
                    else -> R.drawable.class_warrior
                }
                val youtubeLink = when (guildMember.classID) {
                    13 -> "https://www.youtube.com/watch?v=U-RboVyZI0Q"
                    6 -> "https://www.youtube.com/watch?v=STXrxB7ro84"
                    3 -> "https://www.youtube.com/watch?v=mSGWD08x45A"
                    8 -> "https://www.youtube.com/watch?v=gHI4tRcugME&t=242s"
                    7 -> "https://www.youtube.com/watch?v=jbBTELc4yEk"
                    1 -> "https://www.youtube.com/watch?v=0QbAJVh00U4"
                    9 -> "https://www.youtube.com/watch?v=ko-BPn6lCRo"
                    4 -> "https://www.youtube.com/watch?v=mAMX3B80JsA"
                    10 -> "https://www.youtube.com/watch?v=-R71ssAweIY"
                    5 -> "https://www.youtube.com/watch?v=hfV33pCnw_Q"
                    2 -> "https://www.youtube.com/watch?v=iAAWLk9Bw4Y"
                    12 -> "https://www.youtube.com/watch?v=6sFaY8HLbm0"
                    11 -> "https://www.youtube.com/watch?v=gEh6M_ldrwU"
                    else -> "https://www.youtube.com/watch?v=U-RboVyZI0Q"
                }
                val className = when (guildMember.classID) {
                    13 -> "warrior"
                    6 -> "paladin"
                    3 -> "hunter"
                    8 -> "rogue"
                    7 -> "priest"
                    1 -> "death knight"
                    9 -> "shaman"
                    4 -> "mage"
                    10 -> "warlock"
                    5 -> "monk"
                    2 -> "druid"
                    12 -> "demonhunter"
                    11 -> "evoker"
                    else -> "unknown"
                }
                // Character Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = classIcon),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = guildMember.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Level ${guildMember.level ?: "Unknown"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Server: ${guildMember.serverName ?: "Unknown"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Performance Card
                ZoneRankingCard(guildMember)

                ClassTutorialLinkCard(youtubeLink)

                WebBrowserWindow(className)
            } ?: run {
                Text(
                    text = "Character information not found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ZoneRankingCard(member: GuildMember) {
    val rankings = member.getZoneRankings()

    @Composable
    fun getPerformanceColor(percentile: Double?): Color {
        return when {
            percentile == null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            percentile >= 100.0 -> Color(0xFFD2B48C) // Tan
            percentile >= 99.0 -> Color(0xFFFFC0CB) // Pink
            percentile >= 95.0 -> Color(0xFFFFA500) // Orange
            percentile >= 75.0 -> Color(0xFFB266FF) // Lighter Purple
            percentile >= 50.0 -> Color(0xFF4D94FF) // Lighter Blue
            percentile >= 25.0 -> Color(0xFF008000) // Green
            else -> Color(0xFF808080) // Gray
        }
    }

    rankings?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Overall Statistics Section
                Text(
                    text = "Overall Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Best and Median Performance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Best Parse Avg",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = it.bestPerformanceAverage?.toDoubleOrNull()?.let { avg -> String.format("%.2f", avg) } ?: "0.00",
                            style = MaterialTheme.typography.bodyLarge,
                            color = getPerformanceColor(it.bestPerformanceAverage?.toDoubleOrNull())
                        )
                    }
                    Column {
                        Text(
                            text = "Median Performance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = it.medianPerformanceAverage?.toDoubleOrNull()?.let { avg -> String.format("%.2f", avg) } ?: "0.00",
                            style = MaterialTheme.typography.bodyLarge,
                            color = getPerformanceColor(it.medianPerformanceAverage?.toDoubleOrNull())
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // All Stars Section
                it.allStars?.firstOrNull()?.let { allStar ->
                    Text(
                        text = "All Stars Ranking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Points",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${allStar.points?.toDoubleOrNull()?.let { String.format("%.2f", it) } ?: "0.00"}/${allStar.possiblePoints?.toDoubleOrNull()?.let { String.format("%.2f", it) } ?: "0.00"}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = getPerformanceColor(allStar.points?.toDoubleOrNull()?.let { it / (allStar.possiblePoints?.toDoubleOrNull() ?: 1.0) * 100 })
                            )
                        }
                        Column {
                            Text(
                                text = "Server Rank",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "#${allStar.serverRank ?: "-"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Individual Encounter Rankings
                Text(
                    text = "Encounter Rankings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                it.rankings?.filter { ranking ->
                    ranking.rankPercent != null &&
                            ranking.encounter?.name != null &&
                            (ranking.totalKills?.toIntOrNull() ?: 0) > 0
                }?.forEach { ranking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                text = ranking.encounter?.name ?: "Unknown Encounter",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Rank",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${ranking.rankPercent?.toDoubleOrNull()?.let { String.format("%.2f", it) } ?: "0.00"}%",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = getPerformanceColor(ranking.rankPercent?.toDoubleOrNull())
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Kills",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${ranking.totalKills ?: "0"}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassTutorialLinkCard(link: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                context.startActivity(intent)
            }
    ) {
        Text(
            text = "Watch a YouTube guide for your class",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun WebBrowserWindow(className: String) {
    var currentUrl by remember { mutableStateOf<String?>(null) }

    @Composable
    fun getClassGuides(className: String): List<Pair<String, String>> {
        return when (className.lowercase()) {
            "warrior" -> listOf(
                "Arms" to "https://www.wowhead.com/guide/classes/warrior/arms/overview-pve-dps",
                "Fury" to "https://www.wowhead.com/guide/classes/warrior/fury/overview-pve-dps",
                "Protection" to "https://www.wowhead.com/guide/classes/warrior/protection/overview-pve-tank"
            )
            "paladin" -> listOf(
                "Holy" to "https://www.wowhead.com/guide/classes/paladin/holy/overview-pve-healer",
                "Protection" to "https://www.wowhead.com/guide/classes/paladin/protection/overview-pve-tank",
                "Retribution" to "https://www.wowhead.com/guide/classes/paladin/retribution/overview-pve-dps"
            )
            "hunter" -> listOf(
                "Beast Mastery" to "https://www.wowhead.com/guide/classes/hunter/beast-mastery/overview-pve-dps",
                "Marksmanship" to "https://www.wowhead.com/guide/classes/hunter/marksmanship/overview-pve-dps",
                "Survival" to "https://www.wowhead.com/guide/classes/hunter/survival/overview-pve-dps"
            )
            "rogue" -> listOf(
                "Assassination" to "https://www.wowhead.com/guide/classes/rogue/assassination/overview-pve-dps",
                "Outlaw" to "https://www.wowhead.com/guide/classes/rogue/outlaw/overview-pve-dps",
                "Subtlety" to "https://www.wowhead.com/guide/classes/rogue/subtlety/overview-pve-dps"
            )
            "priest" -> listOf(
                "Discipline" to "https://www.wowhead.com/guide/classes/priest/discipline/overview-pve-healer",
                "Holy" to "https://www.wowhead.com/guide/classes/priest/holy/overview-pve-healer",
                "Shadow" to "https://www.wowhead.com/guide/classes/priest/shadow/overview-pve-dps"
            )
            "death knight" -> listOf(
                "Blood" to "https://www.wowhead.com/guide/classes/death-knight/blood/overview-pve-tank",
                "Frost" to "https://www.wowhead.com/guide/classes/death-knight/frost/overview-pve-dps",
                "Unholy" to "https://www.wowhead.com/guide/classes/death-knight/unholy/overview-pve-dps"
            )
            "shaman" -> listOf(
                "Elemental" to "https://www.wowhead.com/guide/classes/shaman/elemental/overview-pve-dps",
                "Enhancement" to "https://www.wowhead.com/guide/classes/shaman/enhancement/overview-pve-dps",
                "Restoration" to "https://www.wowhead.com/guide/classes/shaman/restoration/overview-pve-healer"
            )
            "mage" -> listOf(
                "Arcane" to "https://www.wowhead.com/guide/classes/mage/arcane/overview-pve-dps",
                "Fire" to "https://www.wowhead.com/guide/classes/mage/fire/overview-pve-dps",
                "Frost" to "https://www.wowhead.com/guide/classes/mage/frost/overview-pve-dps"
            )
            "warlock" -> listOf(
                "Affliction" to "https://www.wowhead.com/guide/classes/warlock/affliction/overview-pve-dps",
                "Demonology" to "https://www.wowhead.com/guide/classes/warlock/demonology/overview-pve-dps",
                "Destruction" to "https://www.wowhead.com/guide/classes/warlock/destruction/overview-pve-dps"
            )
            "monk" -> listOf(
                "Brewmaster" to "https://www.wowhead.com/guide/classes/monk/brewmaster/overview-pve-tank",
                "Mistweaver" to "https://www.wowhead.com/guide/classes/monk/mistweaver/overview-pve-healer",
                "Windwalker" to "https://www.wowhead.com/guide/classes/monk/windwalker/overview-pve-dps"
            )
            "druid" -> listOf(
                "Balance" to "https://www.wowhead.com/guide/classes/druid/balance/overview-pve-dps",
                "Feral" to "https://www.wowhead.com/guide/classes/druid/feral/overview-pve-dps",
                "Guardian" to "https://www.wowhead.com/guide/classes/druid/guardian/overview-pve-tank",
                "Restoration" to "https://www.wowhead.com/guide/classes/druid/restoration/overview-pve-healer"
            )
            "demonhunter" -> listOf(
                "Havoc" to "https://www.wowhead.com/guide/classes/demon-hunter/havoc/overview-pve-dps",
                "Vengeance" to "https://www.wowhead.com/guide/classes/demon-hunter/vengeance/overview-pve-tank"
            )
            "evoker" -> listOf(
                "Devastation" to "https://www.wowhead.com/guide/classes/evoker/devastation/overview-pve-dps",
                "Preservation" to "https://www.wowhead.com/guide/classes/evoker/preservation/overview-pve-healer",
                "Augmentation" to "https://www.wowhead.com/guide/classes/evoker/augmentation/overview-pve-dps"
            )
            else -> emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Knapprad
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            getClassGuides(className).forEach { (title, url) ->
                Button(
                    onClick = { currentUrl = url },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 7f)
                    )
                ) {
                    Text(text = title)
                }
            }
        }

        // WebViewsektion
        currentUrl?.let { url ->
            val context = LocalContext.current
            val webView = remember {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                }
            }

            AndroidView(factory = { webView }, update = { it.loadUrl(url) })
        }
    }
}
