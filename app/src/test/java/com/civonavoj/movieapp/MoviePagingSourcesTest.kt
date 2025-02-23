package com.civonavoj.movieapp

import com.civonavoj.movieapp.api.Movie
import com.civonavoj.movieapp.api.MoviesListApiResponse
import com.civonavoj.movieapp.api.TmdbApi
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import androidx.paging.PagingSource
import com.civonavoj.movieapp.api.MoviePagingSource
import com.civonavoj.movieapp.api.SearchPagingSource
import junit.framework.TestCase.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MoviePagingSourcesTest {
    private lateinit var api: TmdbApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test movies paging source load success`() = runTest {
        val testMovie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "poster_path",
            releaseDate = "2024-01-01",
            voteAverage = 8.5
        )
        val response = MoviesListApiResponse(
            page = 1,
            results = listOf(testMovie)
        )

        coEvery {
            api.getPopularMovies(1)
        } returns Response.success(response)

        val pagingSource = MoviePagingSource(api)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(listOf(testMovie), loadResult.data)
        assertEquals(null, loadResult.prevKey)
        assertEquals(2, loadResult.nextKey)
    }

    @Test
    fun `test if search paging source load is successful`() = runTest {
        val testMovie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "poster_path",
            releaseDate = "2024-01-01",
            voteAverage = 8.5
        )

        val response = MoviesListApiResponse(
            page = 1,
            results = listOf(testMovie)
        )

        coEvery {
            api.searchMoviesByKeyword("test", 1)
        } returns Response.success(response)

        val pagingSource = SearchPagingSource(api, "test")

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(listOf(testMovie), loadResult.data)
        assertEquals(null, loadResult.prevKey)
        assertEquals(2, loadResult.nextKey)
    }

    @Test
    fun `test if search paging source handles error`() = runTest {
        coEvery {
            api.getPopularMovies(any())
        } throws Exception("Network error")

        val pagingSource = MoviePagingSource(api)

        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        assertTrue(loadResult is PagingSource.LoadResult.Error)
    }
}
