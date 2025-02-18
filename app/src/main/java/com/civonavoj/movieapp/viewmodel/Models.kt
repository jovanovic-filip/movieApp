package com.civonavoj.movieapp.viewmodel

import com.civonavoj.movieapp.api.Movie

data class MovieDetails(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val posterUrl: String?,
    val rating: Double,
    val overview: String,
    val genres: List<String>,
    val runtime: Int,
    val language: String
)

fun Movie.mapToMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title ?: "",
        releaseDate = releaseDate ?: "",
        posterUrl = posterPath,
        rating = voteAverage ?: 0.0,
        overview = overview ?: "",
        genres = emptyList(),
        runtime = 0,
        language = ""
    )
}

sealed class ApiError {
    data class Network(val code: Int, val message: String) : ApiError()
    data class EmptyResponse(val message: String = "Empty response body") : ApiError()
    data class Unknown(val message: String) : ApiError()
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()
    data class Success(val movie: MovieDetails) : DetailsUiState()
    data class Failed(val error: ApiError?) : DetailsUiState()
}

sealed class MovieListUiState {
    data object Empty : MovieListUiState()
    data object Loading : MovieListUiState()
    data class Success(val movies: List<MovieDetails>) : MovieListUiState()
    data class Failed(val error: ApiError?) : MovieListUiState()
}
