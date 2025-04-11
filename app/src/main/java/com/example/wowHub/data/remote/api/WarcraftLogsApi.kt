package com.example.wowHub.data.remote.api

import com.example.wowHub.data.local.db.entities.Report
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WarcraftLogsApi {
    @GET("reports/guild/{guild}/{server}/{region}")
    suspend fun getReports(
        @Path("guild") guild: String,
        @Path("server") server: String,
        @Path("region") region: String,
        @Query("api_key") apiKey: String
    ): List<Report>
}