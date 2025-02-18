package com.civonavoj.movieapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MoviesListApiResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Response<Movie>

    @GET("search/movie")
    suspend fun searchMoviesByKeyword(@Query("query") query: String): Response<MoviesListApiResponse>

}
