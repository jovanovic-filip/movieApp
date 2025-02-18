package com.civonavoj.movieapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.civonavoj.movieapp.viewmodel.MovieListUiState
import com.civonavoj.movieapp.viewmodel.MovieListViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListViewModelInstrumentedTest {
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        viewModel = MovieListViewModel()
    }

    @Test
    fun testPopularMoviesApiCall() = runBlocking {
        val states = mutableListOf<MovieListUiState>()
        val job = launch {
            viewModel.uiState.collect { state -> states.add(state) }
        }

        viewModel.fetchPopularMovies()
        delay(1000)
        job.cancel()

        assertTrue("Initial state should be Loading", states[0] is MovieListUiState.Loading)
        assertTrue("Final state should be Success", states.last() is MovieListUiState.Success)

        val movies = (states.last() as MovieListUiState.Success).movies
        assertTrue("Movie list should not be empty", movies.isNotEmpty())
        assertTrue("Should have at least 10 movies", movies.size >= 10)
    }

    @Test
    fun testSearchMoviesByKeywordApiCall() = runBlocking {
        val states = mutableListOf<MovieListUiState>()
        val job = launch {
            viewModel.uiState.collect { state ->
                states.add(state)
            }
        }

        viewModel.searchMoviesByKeyword("Inception")
        delay(1000)
        job.cancel()

        assertTrue("Initial state should be Loading", states[0] is MovieListUiState.Loading)
        assertTrue("Final state should be Success", states.last() is MovieListUiState.Success)

        val movies = (states.last() as MovieListUiState.Success).movies
        assertTrue("Movie list should not be empty", movies.isNotEmpty())
        assertTrue("Should have at least 1 movie", movies.isNotEmpty())
    }
}
