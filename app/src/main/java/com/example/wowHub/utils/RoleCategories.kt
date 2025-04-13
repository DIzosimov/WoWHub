package com.example.wowHub.utils

import com.example.wowHub.data.local.db.entities.WoWAuditMember

enum class RoleCategory {
    TANK, HEALER, RANGED_DPS, MELEE_DPS
}

object RoleCategories {
    fun getRoleCategory(member: WoWAuditMember): RoleCategory {
        val role = member.characterRole.lowercase()
        val wowClass = member.characterClass.lowercase()

        return when {
            role == "tank" -> RoleCategory.TANK
            role == "healer" || isHealerClass(wowClass) -> RoleCategory.HEALER
            isRangedDpsClass(wowClass) -> RoleCategory.RANGED_DPS
            else -> RoleCategory.MELEE_DPS
        }
    }

    private fun isHealerClass(wowClass: String): Boolean {
        return when (wowClass) {
            "priest", "paladin", "shaman", "druid", "monk" -> true
            else -> false
        }
    }

    private fun isRangedDpsClass(wowClass: String): Boolean {
        return when (wowClass) {
            "mage", "warlock", "hunter", "evoker" -> true
            else -> false
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