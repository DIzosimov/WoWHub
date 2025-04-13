package com.example.wowHub.data.remote.api

import com.example.wowHub.data.remote.models.WoWAuditResponse
import retrofit2.http.GET
import retrofit2.http.Header

/*interface WoWAuditApi {
    @GET("guilds/{region}/{realm}/{guild}/json")
    suspend fun getRoster(
        @Header("Authorization") authHeader: String,
        @Path("region") region: String,
        @Path("realm") realm: String,
        @Path("guild") guild: String
    ): WoWAuditData
}*/

interface WoWAuditApi {
    @GET("v1/characters")
    suspend fun getRoster(
        @Header("Authorization") authHeader: String
    ): List<WoWAuditResponse>
}