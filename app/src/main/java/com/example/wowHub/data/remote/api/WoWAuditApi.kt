package com.example.wowHub.data.remote.api

import com.example.wowHub.data.local.db.entities.WoWAuditData
import retrofit2.http.GET
import retrofit2.http.Path

interface WoWAuditApi {
    @GET("guilds/{region}/{realm}/{guild}/json")
    suspend fun getRoster(
        @Path("region") region: String,
        @Path("realm") realm: String,
        @Path("guild") guild: String
    ): WoWAuditData
}