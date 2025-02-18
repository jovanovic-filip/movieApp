package com.civonavoj.movieapp

import app.cash.turbine.test
import com.civonavoj.movieapp.api.Movie
import com.civonavoj.movieapp.api.RetrofitClient
import com.civonavoj.movieapp.api.TmdbApi
import com.civonavoj.movieapp.viewmodel.ApiError
import com.civonavoj.movieapp.viewmodel.DetailsUiState
import com.civonavoj.movieapp.viewmodel.MovieDetailsViewModel
import com.civonavoj.movieapp.viewmodel.mapToMovieDetails
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
class MovieDetailsViewModelTest {
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var api: TmdbApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        RetrofitClient.setApiForTesting(api)
        viewModel = MovieDetailsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchMovieDetails_success() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "poster_path",
            releaseDate = "2024-01-01",
            voteAverage = 8.5
        )

        coEvery { api.getMovieDetails(1) } returns Response.success(movie)

        viewModel.uiState.test {
            assert(awaitItem() is DetailsUiState.Loading) {
                "Initial state should be Loading"
            }
            viewModel.fetchMovieDetails(1)
            val successState = awaitItem() as DetailsUiState.Success
            assert(successState.movie == movie.mapToMovieDetails()) {
                "Expected movie details to match the mock data"
            }
        }
    }

    @Test
    fun fetchMovieDetails_error() = runTest {
        coEvery { api.getMovieDetails(1) } throws Exception("Network error")
        viewModel.uiState.test {
            assert(awaitItem() is DetailsUiState.Loading) {
                "Initial state should be Loading"
            }
            viewModel.fetchMovieDetails(1)
            val failure = awaitItem() as DetailsUiState.Failed
            assert(failure.error is ApiError.Unknown) {
                "Error message should match the exception message"
            }
        }
    }
}
