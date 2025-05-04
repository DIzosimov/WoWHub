package com.example.wowHub.data.remote.models

import com.example.wowHub.data.local.db.entities.ZoneRankings

data class WarcraftCharacterResponse(
    val data: CharacterDataWrapper?
)

data class CharacterDataWrapper(
    val characterData: CharacterContainer?
)

data class CharacterContainer(
    val character: CharacterDetails?
)

data class CharacterDetails(
    val id: Int,
    val canonicalID: Int,
    val name: String,
    val classID: Int,
    val level: Int,
    val faction: String,
    val guildRank: Int,
    val server: CharacterServer,
    val zoneRankings: ZoneRankings
)

data class CharacterServer(
    val name: String,
    val slug: String
)
