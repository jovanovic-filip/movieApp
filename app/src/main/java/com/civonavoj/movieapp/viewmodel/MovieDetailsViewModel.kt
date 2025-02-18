package com.civonavoj.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civonavoj.movieapp.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MovieDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.emit(DetailsUiState.Loading)
            try {
                RetrofitClient.api.getMovieDetails(movieId).let { response ->
                    when {
                        response.isSuccessful -> response.body()?.let { item ->
                            DetailsUiState.Success(item.mapToMovieDetails())
                        } ?: DetailsUiState.Failed(ApiError.EmptyResponse())
                        else -> DetailsUiState.Failed(ApiError.Network(response.code(), response.message()))
                    }
                }
            } catch (e: Exception) {
               DetailsUiState.Failed(ApiError.Unknown(e.message ?: "Unknown error occurred"))
            }.let {
                _uiState.emit(it)
            }
        }
    }
}
