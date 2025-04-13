package com.example.wowHub.utils

import androidx.compose.ui.graphics.Color

object ClassColors {
    val warrior = Color(0xFFC79C6E)
    val paladin = Color(0xFFF58CBA)
    val hunter = Color(0xFFABD473)
    val rogue = Color(0xFFFFF569)
    val priest = Color(0xFFFFFFFF)
    val deathKnight = Color(0xFFC41F3B)
    val shaman = Color(0xFF0070DE)
    val mage = Color(0xFF69CCF0)
    val warlock = Color(0xFF9482C9)
    val monk = Color(0xFF00FF96)
    val druid = Color(0xFFFF7D0A)
    val demonHunter = Color(0xFFA330C9)
    val evoker = Color(0xFF33937F)

    fun getClassColor(className: String): Color {
        return when (className.lowercase()) {
            "warrior" -> warrior
            "paladin" -> paladin
            "hunter" -> hunter
            "rogue" -> rogue
            "priest" -> priest
            "death knight" -> deathKnight
            "shaman" -> shaman
            "mage" -> mage
            "warlock" -> warlock
            "monk" -> monk
            "druid" -> druid
            "demon hunter" -> demonHunter
            "evoker" -> evoker
            else -> Color.Gray
        }
    }
} 