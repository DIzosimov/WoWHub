package com.example.wowHub.utils

import com.example.wowHub.data.local.db.entities.WoWAuditMember

enum class RoleCategory {
    TANK, HEALER, RANGED_DPS, MELEE_DPS
}

object RoleCategories {
    fun getRoleCategory(member: WoWAuditMember): RoleCategory {
        return when (member.characterRole.lowercase()) {
            "tank" -> RoleCategory.TANK
            "healer" -> RoleCategory.HEALER
            in listOf("mage", "warlock", "priest", "shaman", "druid", "hunter", "evoker") -> RoleCategory.RANGED_DPS
            else -> RoleCategory.MELEE_DPS
        }
    }

    fun getRoleDisplayName(category: RoleCategory): String {
        return when (category) {
            RoleCategory.TANK -> "Tanks"
            RoleCategory.HEALER -> "Healers"
            RoleCategory.RANGED_DPS -> "Ranged DPS"
            RoleCategory.MELEE_DPS -> "Melee DPS"
        }
    }
} 