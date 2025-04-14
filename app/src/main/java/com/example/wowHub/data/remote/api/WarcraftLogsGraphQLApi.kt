package com.example.wowHub.data.remote.api


import com.example.wowHub.data.remote.GraphQL.GraphQLRequest
import com.example.wowHub.data.remote.models.WarcraftCharacterResponse
import com.example.wowHub.data.remote.models.WarcraftLogsResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WarcraftLogsGraphQLApi {
    @POST("client")
    suspend fun getReports(
        @Header("Authorization") auth: String,
        @Body body: GraphQLRequest
    ): WarcraftLogsResponse

    @POST("client")
    suspend fun getCharacter(
        @Header("Authorization") auth: String,
        @Body body: GraphQLRequest
    ): WarcraftCharacterResponse
}