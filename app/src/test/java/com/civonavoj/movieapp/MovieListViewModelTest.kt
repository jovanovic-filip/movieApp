package com.civonavoj.movieapp

import app.cash.turbine.test
import com.civonavoj.movieapp.api.Movie
import com.civonavoj.movieapp.api.MoviesListApiResponse
import com.civonavoj.movieapp.api.RetrofitClient
import com.civonavoj.movieapp.api.TmdbApi
import com.civonavoj.movieapp.viewmodel.ApiError
import com.civonavoj.movieapp.viewmodel.mapToMovieDetails
import com.civonavoj.movieapp.viewmodel.MovieListUiState
import com.civonavoj.movieapp.viewmodel.MovieListViewModel
import io.mockk.coEvery
import io.mockk.mockk
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

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {
    private lateinit var viewModel: MovieListViewModel
    private lateinit var api: TmdbApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        RetrofitClient.setApiForTesting(api)
        viewModel = MovieListViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPopularMovies_success() = runTest {
        val movies = listOf(
            Movie(
                id = 1,
                title = "Test Movie",
                overview = "Overview",
                posterPath = "poster_path",
                releaseDate = "2024-01-01",
                voteAverage = 8.5
            )
        )
        val response = MoviesListApiResponse(1, movies)

        coEvery { api.getPopularMovies() } returns Response.success(response)

        viewModel.uiState.test {
            assert(awaitItem() is MovieListUiState.Loading) {
                "Initial state should be Loading"
            }
            viewModel.fetchPopularMovies()
            val successState = awaitItem() as MovieListUiState.Success
            assert(successState.movies.containsAll(movies.map { it.mapToMovieDetails() })) {
                "Expected movies list to match the mock data"
            }
        }
    }

    @Test
    fun fetchPopularMovies_error() = runTest {
        coEvery { api.getPopularMovies() } throws Exception("Network error")
        viewModel.uiState.test {
            assert(awaitItem() is MovieListUiState.Loading) {
                "Initial state should be Loading"
            }
            viewModel.fetchPopularMovies()
            val failure = awaitItem() as MovieListUiState.Failed
            assert(failure.error is ApiError.Network) {
                "Error message should match the exception message"
            }
        }
    }
}
