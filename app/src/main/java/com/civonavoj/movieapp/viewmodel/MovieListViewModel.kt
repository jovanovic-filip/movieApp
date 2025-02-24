package com.civonavoj.movieapp.viewmodel

import android.content.Context
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import androidx.paging.PagingData
import com.civonavoj.movieapp.data.Cache
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieListViewModel(
    private val api: TmdbApi = RetrofitClient.api
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var currentSearchQuery = MutableStateFlow("")

    private var _previousSearchQueries = MutableStateFlow<List<String>>(emptyList())
    val previousSearchQueries = _previousSearchQueries.asStateFlow()

    val popularMovies = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(api) }
    ).flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = currentSearchQuery.flatMapLatest { query ->
        when {
            query.isEmpty() -> flowOf(PagingData.empty())
            else -> Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
                pagingSourceFactory = { SearchPagingSource(api, query) }
            ).flow
        }
    }.cachedIn(viewModelScope)

    fun searchMovies(query: String, context: Context) {
        currentSearchQuery.value = query
        Cache.saveSearchQuery(context, query)
        _previousSearchQueries.update { Cache.getSearchQueries(context).reversed() }
    }
}
