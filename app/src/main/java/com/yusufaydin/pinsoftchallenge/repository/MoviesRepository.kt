package com.yusufaydin.pinsoftchallenge.repository

import com.yusufaydin.pinsoftchallenge.api.RetrofitInstance
import com.yusufaydin.pinsoftchallenge.util.Constants.Companion.API_KEY

class MoviesRepository {
    suspend fun getSearchedMovie(movieName: String, pageNumber: Int) =
        RetrofitInstance.api.getSearchedMovie(movieName, API_KEY, pageNumber)

    suspend fun getMovieDetails(movieId: String) = RetrofitInstance.api.getMovieDetails(
        movieId,
        API_KEY
    )
}