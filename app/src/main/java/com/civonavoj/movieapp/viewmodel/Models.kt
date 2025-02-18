package com.civonavoj.movieapp.viewmodel

import com.civonavoj.movieapp.api.Movie

data class MovieItem (
    val id: Int,
    val title: String,
    val rating: Double,
    val imageUrl: String?
)

fun Movie.mapToMovieItem(): MovieItem {
    return MovieItem(
        id = id,
        title = title ?: "",
        rating = voteAverage ?: 0.0,
        imageUrl = posterPath
    )
}

sealed class ApiError {
    data class Network(val code: Int, val message: String) : ApiError()
    data class EmptyResponse(val message: String = "Empty response body") : ApiError()
    data class Unknown(val message: String) : ApiError()
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()
    data class Success(val movie: MovieItem) : DetailsUiState()
    data class Failed(val error: ApiError?) : DetailsUiState()
}

sealed class MovieListUiState {
    data object Empty : MovieListUiState()
    data object Loading : MovieListUiState()
    data class Success(val movies: List<MovieItem>) : MovieListUiState()
    data class Failed(val error: ApiError?) : MovieListUiState()
}
