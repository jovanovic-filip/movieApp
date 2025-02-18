package com.civonavoj.movieapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.civonavoj.movieapp.viewmodel.DetailsUiState
import com.civonavoj.movieapp.viewmodel.MovieDetailsViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailsViewModelInstrumentedTest {
    private lateinit var viewModel: MovieDetailsViewModel

    @Before
    fun setup() {
        viewModel = MovieDetailsViewModel()
    }

    @Test
    fun testFetchMovieDetailsApiCall() = runBlocking {
        val states = mutableListOf<DetailsUiState>()
        val job = launch {
            viewModel.uiState.collect { state -> states.add(state) }
        }

        viewModel.fetchMovieDetails(27205)
        delay(1000)
        job.cancel()

        assertTrue("Initial state should be Loading", states[0] is DetailsUiState.Loading)
        assertTrue("Final state should be Success", states.last() is DetailsUiState.Success)
    }
}
