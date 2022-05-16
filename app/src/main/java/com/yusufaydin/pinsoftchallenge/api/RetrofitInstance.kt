package com.yusufaydin.pinsoftchallenge.api

import com.yusufaydin.pinsoftchallenge.util.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: MoviesAPI by lazy {
        retrofit.create(MoviesAPI::class.java)
    }
}