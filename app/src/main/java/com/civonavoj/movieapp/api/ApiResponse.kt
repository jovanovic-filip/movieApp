package com.civonavoj.movieapp.api

import com.google.gson.annotations.SerializedName

data class MoviesListApiResponse(
    val page: Int,
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("original_language")
    val language: String?
)
