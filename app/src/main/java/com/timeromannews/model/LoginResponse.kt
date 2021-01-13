package com.timeromannews.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("expires_at")
    val expires_at: String,
    @SerializedName("scheme")
    val scheme: String,
    @SerializedName("token")
    val token: String
)