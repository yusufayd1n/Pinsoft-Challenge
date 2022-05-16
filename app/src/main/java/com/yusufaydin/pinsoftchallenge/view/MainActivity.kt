package com.yusufaydin.pinsoftchallenge.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yusufaydin.pinsoftchallenge.adapter.MoviesAdapter
import com.yusufaydin.pinsoftchallenge.databinding.ActivityMainBinding

import com.yusufaydin.pinsoftchallenge.repository.MoviesRepository
import com.yusufaydin.pinsoftchallenge.util.Constants.Companion.QUERY_PAGE_SIZE
import com.yusufaydin.pinsoftchallenge.util.Resource
import com.yusufaydin.pinsoftchallenge.util.hide
import com.yusufaydin.pinsoftchallenge.util.show
import com.yusufaydin.pinsoftchallenge.viewmodel.MoviesViewModel

import com.yusufaydin.pinsoftchallenge.viewmodel.MoviesViewModelFactory


import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MoviesViewModel
    private lateinit var binding: ActivityMainBinding
    lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModelFactory = MoviesViewModelFactory(MoviesRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(MoviesViewModel::class.java)
        setUpMovieRecyclerView()
        viewModel.getSearchedMovie(binding.movieNameEditTextView.text.toString())
        searchEdittextChangeListener()
        observeMoviesData()
    }

    private fun searchEdittextChangeListener() {
        binding.movieNameEditTextView.addTextChangedListener { editable ->
            lifecycleScope.launch {
                delay(3000)
                editable?.let {
                    viewModel.moviesPageNumber = 1
                    if (editable.toString().isNotEmpty()) {
                        if (editable.length > 2) {
                            if (isConnected() == true) {
                                viewModel.searchBoolean = true
                                closeKeyboard()
                                viewModel.getSearchedMovie(editable.toString())
                            } else printToast("No Internet")
                        } else printToast("Please Try Longer Movie Name")
                    } else printToast("Please Enter Movie Name")
                }
            }
        }
    }

    private fun printToast(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getSearchedMovie(binding.movieNameEditTextView.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling =
                true
        }
    }

    private fun observeMoviesData() {
        viewModel.searchedMovies.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { movieResponse ->
                        moviesAdapter.differ.submitList(movieResponse.search.toList())
                        val totalPages = movieResponse.totalResults.toInt() / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.moviesPageNumber == totalPages
                    }
                    if (isLastPage) {
                        binding.moviesRecyclerView.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, "No Movie Found", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.hide()
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.show()
        isLoading = true
    }


    private fun setUpMovieRecyclerView() {
        moviesAdapter = MoviesAdapter()
        binding.moviesRecyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(this@MainActivity.scrollListener)
        }
        moviesAdapter.setOnItemCLickListener {
            val intentToDetails = Intent(this, MoviesDetail::class.java)
            intentToDetails.putExtra("movieId", it.imdbID)
            startActivity(intentToDetails)
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val hideMe = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun isConnected(): Boolean {
        var connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var network: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (network != null) {
            if (network.isConnected) {
                return true
            }
        }
        return false
    }
}

