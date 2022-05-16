package com.yusufaydin.pinsoftchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusufaydin.pinsoftchallenge.model.MovieDetailModel
import com.yusufaydin.pinsoftchallenge.model.MovieModel
import com.yusufaydin.pinsoftchallenge.repository.MoviesRepository
import com.yusufaydin.pinsoftchallenge.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception


class MoviesViewModel(
    private val repository: MoviesRepository
) : ViewModel() {
    var searchMovieResponse: MovieModel? = null
    val searchedMovies: MutableLiveData<Resource<MovieModel>> = MutableLiveData()
    var moviesPageNumber = 1
    var searchBoolean: Boolean = true

    val selectedMovieDetail: MutableLiveData<Resource<MovieDetailModel>> = MutableLiveData()

    fun getSelectedMovie(movieId: String) = viewModelScope.launch {
        selectedMovieDetail.postValue(Resource.Loading())
        val response = repository.getMovieDetails(movieId)
        selectedMovieDetail.postValue(handleSelectedMovieResponse(response))
    }

    fun getSearchedMovie(movieName: String) {
        viewModelScope.launch {
            searchedMovies.postValue(Resource.Loading())
            val response = repository.getSearchedMovie(movieName, moviesPageNumber)
            searchedMovies.postValue(handleSearchMovieResponse(response))
        }
    }

    private fun handleSelectedMovieResponse(response: Response<MovieDetailModel>): Resource<MovieDetailModel> {
        try {
            if (response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    return Resource.Success(resultResponse)
                }
            }
            return Resource.Error(response.message())
        } catch (e: Exception) {
            searchedMovies.postValue(Resource.Loading())
            moviesPageNumber = 1
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchMovieResponse(response: Response<MovieModel>): Resource<MovieModel> {
        try {
            if (response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    moviesPageNumber++
                    if (searchMovieResponse == null) {
                        searchMovieResponse = resultResponse
                    } else {
                        if (searchBoolean) {
                            val oldMovies = searchMovieResponse?.search!!
                            oldMovies.clear()
                            val newMovies = resultResponse.search
                            oldMovies.addAll(newMovies)
                        } else {
                            val oldMovies = searchMovieResponse?.search!!
                            val newMovies = resultResponse.search
                            oldMovies.addAll(newMovies)
                        }
                    }
                    if (searchBoolean) {
                        searchBoolean = false
                        return Resource.Success(resultResponse)
                    } else {
                        return Resource.Success(searchMovieResponse ?: resultResponse)
                    }
                }
            }
        } catch (e: Exception) {
            searchedMovies.postValue(Resource.Loading())
            moviesPageNumber = 1
        }
        return Resource.Error(response.message())
    }
}