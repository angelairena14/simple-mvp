package com.timeromannews.model

data class RetrofitResponse(
    val code : Int,
    val message : String,
    val error: String,
    val name: String
)