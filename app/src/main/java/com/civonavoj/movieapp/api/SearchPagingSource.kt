package com.civonavoj.movieapp.api

import androidx.paging.PagingSource
import androidx.paging.PagingState

class SearchPagingSource(
    private val api: TmdbApi,
    private val query: String
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = api.searchMoviesByKeyword(query, page)
            when {
                response.isSuccessful -> {
                    val movies = response.body()?.results.orEmpty()
                    val nextKey = if (movies.isEmpty()) null else page + 1
                    LoadResult.Page(
                        data = movies,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextKey
                    )
                }
                else -> LoadResult.Error(Exception("API call failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
