package com.civonavoj.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civonavoj.movieapp.api.MoviesListApiResponse
import com.civonavoj.movieapp.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private fun handleApiResponse(response: Response<MoviesListApiResponse>) {
        viewModelScope.launch {
            _uiState.emit(MovieListUiState.Loading)
            try {
                when {
                    response.isSuccessful -> response.body()?.let { page ->
                        MovieListUiState.Success(page.results.map { it.mapToMovieDetails() })
                    } ?: MovieListUiState.Failed(
                        ApiError.EmptyResponse()
                    )
                    else -> MovieListUiState.Failed(
                        ApiError.Network(response.code(), response.message())
                    )
                }
            } catch (e: Exception) {
                MovieListUiState.Failed(ApiError.Unknown(e.message ?: "Unknown error occurred"))
            }.let {
                _uiState.emit(it)
            }
        }
    }

    fun fetchPopularMovies() = viewModelScope.launch {
        handleApiResponse(RetrofitClient.api.getPopularMovies())
    }

    fun searchMoviesByKeyword(keyword: String) = viewModelScope.launch {
        handleApiResponse(RetrofitClient.api.searchMoviesByKeyword(keyword))
    }

}
