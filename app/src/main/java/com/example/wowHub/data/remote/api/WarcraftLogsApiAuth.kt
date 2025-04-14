package com.example.wowHub.data.remote.api

import com.example.wowHub.data.remote.models.AuthTokenResponse
import retrofit2.http.*

interface WarcraftLogsAuthApi {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun fetchToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AuthTokenResponse
}