package com.example.wowHub.utils

import android.util.Log
import com.example.wowHub.data.remote.api.WarcraftLogsAuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenManager(
    private val authApi: WarcraftLogsAuthApi,
    private val clientId: String,
    private val clientSecret: String
) {
    private var accessToken: String? = null
    private var expiryTime: Long = 0L

    suspend fun getValidToken(): String {
        val now = System.currentTimeMillis() / 1000
        if (accessToken == null || now >= expiryTime) {
            try {
                val response = withContext(Dispatchers.IO) {
                    authApi.fetchToken(
                        clientId = clientId,
                        clientSecret = clientSecret
                    )
                }
                accessToken = response.accessToken
                expiryTime = now + response.expiresIn - 60 // renew 1 min early
            } catch (e: Exception) {
                Log.e("TokenManager", "Error fetching token", e)
                throw e
            }
        }
        return accessToken!!
    }
}