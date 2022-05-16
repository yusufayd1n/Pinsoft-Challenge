package com.yusufaydin.pinsoftchallenge.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.yusufaydin.pinsoftchallenge.databinding.ActivityMoviesDetailBinding
import com.yusufaydin.pinsoftchallenge.repository.MoviesRepository
import com.yusufaydin.pinsoftchallenge.util.Resource
import com.yusufaydin.pinsoftchallenge.util.hide
import com.yusufaydin.pinsoftchallenge.util.loadImage
import com.yusufaydin.pinsoftchallenge.util.show
import com.yusufaydin.pinsoftchallenge.viewmodel.MoviesViewModel
import com.yusufaydin.pinsoftchallenge.viewmodel.MoviesViewModelFactory


class MoviesDetail : AppCompatActivity() {
    lateinit var viewModel: MoviesViewModel
    private lateinit var binding: ActivityMoviesDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val movieId = intent.getStringExtra("movieId")
        val viewModelFactory = MoviesViewModelFactory(MoviesRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(MoviesViewModel::class.java)
        viewModel.getSelectedMovie(movieId!!)
        loadSelectedMovie()
    }

    private fun loadSelectedMovie() {
        viewModel.selectedMovieDetail.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieDetailResponse ->
                        binding.apply {
                            movieDetailImageView.loadImage(movieDetailResponse.poster)
                            movieDetailTitleTextView.text = movieDetailResponse.title
                            movieDetailActorsTextView.text = "Actor: " + movieDetailResponse.actors
                            movieDetailGenreTextView.text = "Genre: " + movieDetailResponse.genre
                            movieDetailAwardsTextView.text = "Awards: " + movieDetailResponse.awards
                            movieDetailBoxOfficeTextView.text =
                                "BoxOffice: " + movieDetailResponse.boxOffice
                            movieDetailImdbTextView.text = "Imdb: " + movieDetailResponse.imdbRating
                            movieDetailRuntimeTextView.text =
                                "Runtime: " + movieDetailResponse.runtime
                            movieDetailLanguageTextView.text =
                                "Language: " + movieDetailResponse.language
                            movieDetailPlotTextView.text = "Plot: " + movieDetailResponse.plot

                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.detailPaginationProgressBar.hide()
    }

    private fun showProgressBar() {
        binding.detailPaginationProgressBar.show()
    }
}