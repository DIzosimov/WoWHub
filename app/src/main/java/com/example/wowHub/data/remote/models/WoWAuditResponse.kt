package com.example.wowHub.data.remote.models

import com.google.gson.annotations.SerializedName

data class WoWAuditResponse(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("class")
    val wowClass: String? = null,
    
    @SerializedName("role")
    val role: String? = null,
    
    @SerializedName("attendance")
    val attendance: Float? = null
) 