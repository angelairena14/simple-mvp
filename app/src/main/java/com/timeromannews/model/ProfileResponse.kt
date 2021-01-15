package com.timeromannews.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("bio")
    val bio: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("web")
    val web: String
)