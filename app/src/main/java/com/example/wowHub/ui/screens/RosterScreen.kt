package com.example.wowHub.ui.screens

import com.example.wowHub.utils.RoleCategories
import com.example.wowHub.utils.RoleCategory
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wowHub.R
import com.example.wowHub.data.local.db.entities.WoWAuditMember
import com.example.wowHub.viewmodel.GuildRepository

@Composable
fun WoWAuditRosterScreen(
    viewModel: GuildRepository.GuildViewModel,
    modifier: Modifier = Modifier
) {
    val roster by viewModel.auditRoster.observeAsState(emptyList())
    
    // Group members by role category
    val groupedMembers = remember(roster) {
        roster.groupBy { RoleCategories.getRoleCategory(it) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Iterate through each role category
        RoleCategory.entries.forEach { category ->
            val membersInCategory = groupedMembers[category] ?: emptyList()
            if (membersInCategory.isNotEmpty()) {
                item {
                    RoleCategorySection(
                        category = category,
                        members = membersInCategory
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RoleCategorySection(
    category: RoleCategory,
    members: List<WoWAuditMember>
) {
    var expanded by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Category header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Role icon
                    val roleIcon = when (category) {
                        RoleCategory.TANK -> R.drawable.role_tank
                        RoleCategory.HEALER -> R.drawable.role_healer
                        RoleCategory.MELEE_DPS -> R.drawable.role_melee
                        RoleCategory.RANGED_DPS -> R.drawable.rdps
                    }
                    
                    Image(
                        painter = painterResource(id = roleIcon),
                        contentDescription = "${category.name} role icon",
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Text(
                        text = RoleCategories.getRoleDisplayName(category),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            // Animated member list
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = tween(300)
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300)
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                Column {
                    members.forEach { member ->
                        MemberCard(member = member)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberCard(
    member: WoWAuditMember,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Class icon
            val classIcon = when (member.characterClass.lowercase()) {
                "death knight" -> R.drawable.class_death_knight
                "demon hunter" -> R.drawable.class_demonhunter
                "druid" -> R.drawable.class_druid
                "evoker" -> R.drawable.class_evoker
                "hunter" -> R.drawable.class_hunter
                "mage" -> R.drawable.class_mage
                "monk" -> R.drawable.class_monk
                "paladin" -> R.drawable.class_paladin
                "priest" -> R.drawable.class_priest
                "rogue" -> R.drawable.class_rogue
                "shaman" -> R.drawable.class_shaman
                "warlock" -> R.drawable.class_warlock
                "warrior" -> R.drawable.class_warrior
                else -> R.drawable.class_warrior // Default icon
            }
            
            Image(
                painter = painterResource(id = classIcon),
                contentDescription = "${member.characterClass} icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )

            // Character name and attendance
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.characterName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Attendance: ${member.attendance}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}