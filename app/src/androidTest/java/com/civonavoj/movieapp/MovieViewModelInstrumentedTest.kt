package com.civonavoj.movieapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieViewModelInstrumentedTest {
    private lateinit var viewModel: MovieViewModel

    @Before
    fun setup() {
        viewModel = MovieViewModel()
    }

    @Test
    fun testPopularApiCall() = runBlocking {
        val states = mutableListOf<MovieUiState>()
        val job = launch {
            viewModel.uiState.collect { state -> states.add(state) }
        }

        viewModel.fetchPopularMovies()
        delay(5000)
        job.cancel()

        assertTrue("Initial state should be Loading", states[0] is MovieUiState.Loading)
        assertTrue("Final state should be Success", states.last() is MovieUiState.Success)

        val movies = (states.last() as MovieUiState.Success).movies
        assertTrue("Movie list should not be empty", movies.isNotEmpty())
        assertTrue("Should have at least 10 movies", movies.size >= 10)
        assertTrue("First movie should have a title", movies.first().title.isNotEmpty())
        assertTrue("First movie should have an overview", movies.first().overview.isNotEmpty())
    }
}
