package com.civonavoj.movieapp.api

import androidx.annotation.VisibleForTesting
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private var _api: TmdbApi? = null

    val api: TmdbApi
        get() = _api ?: createApi().also { _api = it }

    @VisibleForTesting
    fun setApiForTesting(testApi: TmdbApi) {
        _api = testApi
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private fun createApi(): TmdbApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}
