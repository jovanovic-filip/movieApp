package com.civonavoj.movieapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civonavoj.movieapp.api.Movie
import com.civonavoj.movieapp.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    fun fetchPopularMovies() {
        viewModelScope.launch {
            _uiState.emit(MovieUiState.Loading)
            try {
                RetrofitClient.api.getPopularMovies().let { response ->
                    when {
                        response.isSuccessful -> {
                            response.body()?.let { movieResponse ->
                                _uiState.value = MovieUiState.Success(movieResponse.results)
                            } ?: run {
                                _uiState.value = MovieUiState.Error("Empty response body")
                            }
                        }
                        else -> {
                            _uiState.value = MovieUiState.Error(
                                "Error: ${response.code()} ${response.message()}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MovieUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

sealed class MovieUiState {
    data object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
}
