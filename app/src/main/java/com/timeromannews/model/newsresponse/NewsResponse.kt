package com.timeromannews.model.newsresponse

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("data")
    val `data`: ArrayList<News>
)
data class News(
    @SerializedName("channel")
    val channel: Channel,
    @SerializedName("counter")
    val counter: Counter,
    @SerializedName("cover_image")
    val cover_image: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nsfw")
    val nsfw: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)

data class Counter(
    @SerializedName("comment")
    val comment: Int,
    @SerializedName("downvote")
    val downvote: Int,
    @SerializedName("upvote")
    val upvote: Int,
    @SerializedName("view")
    val view: Int
)

data class Channel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)