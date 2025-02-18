package com.civonavoj.movieapp.api

import retrofit2.Response
import retrofit2.http.GET

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>

}
