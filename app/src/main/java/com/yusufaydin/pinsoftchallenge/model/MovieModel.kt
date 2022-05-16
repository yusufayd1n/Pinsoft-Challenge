package com.yusufaydin.pinsoftchallenge.model


import com.google.gson.annotations.SerializedName


data class MovieModel(
    @SerializedName("Response")
    val response: String,
    @SerializedName("Search")
    val search: MutableList<Search>,
    @SerializedName("totalResults")
    val totalResults: String
)