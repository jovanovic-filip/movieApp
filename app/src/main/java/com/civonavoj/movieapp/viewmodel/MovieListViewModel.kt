package com.civonavoj.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.civonavoj.movieapp.api.MoviePagingSource
import com.civonavoj.movieapp.api.RetrofitClient
import com.civonavoj.movieapp.api.SearchPagingSource
import com.civonavoj.movieapp.api.TmdbApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class MovieListViewModel(
    private val api: TmdbApi = RetrofitClient.api
) : ViewModel() {

    private var currentSearchQuery = MutableStateFlow("")

    val popularMovies = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { MoviePagingSource(api) }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = currentSearchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            emptyFlow()
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { SearchPagingSource(api, query) }
            ).flow
        }
    }.cachedIn(viewModelScope)

    fun searchMovies(query: String) {
        currentSearchQuery.value = query
    }
}
