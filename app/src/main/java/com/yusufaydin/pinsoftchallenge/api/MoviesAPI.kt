package com.yusufaydin.pinsoftchallenge.api

import com.yusufaydin.pinsoftchallenge.model.MovieDetailModel
import com.yusufaydin.pinsoftchallenge.model.MovieModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesAPI {
    //https://www.omdbapi.com/?t=speed&apikey=4c033f59

    @GET("?")
    suspend fun getSearchedMovie(
        @Query("s") title: String,
        @Query("apikey") apiKey: String,
        @Query("page") pageNumber: Int,
        @Query("type") movieType: String = "movie"
    ): Response<MovieModel>

    @GET("?")
    suspend fun getMovieDetails(
        @Query("i") title: String,
        @Query("apikey") apiKey: String,
    ): Response<MovieDetailModel>

}