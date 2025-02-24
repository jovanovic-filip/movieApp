package com.civonavoj.movieapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.civonavoj.movieapp.viewmodel.DetailsUiState
import com.civonavoj.movieapp.viewmodel.MovieDetailsViewModel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun fetchMovieDetails_shouldEmitLoadingThenSuccess() = runTest {
        // When
        viewModel.fetchMovieDetails(27205)
        val states = viewModel.uiState.take(2).toList()
        // Then
        assertTrue(states[0] is DetailsUiState.Loading)
        assertTrue(states[1] is DetailsUiState.Success)

        val successState = states[1] as DetailsUiState.Success
        assertEquals(27205, successState.movie.id)
    }

    @Test
    fun fetchMovieDetails_shouldEmitErrorOnFailure() = runTest {
        // When
        viewModel.fetchMovieDetails(-1) // Invalid ID to trigger error
        val states = viewModel.uiState.take(2).toList()
        // Then
        assertTrue(states[0] is DetailsUiState.Loading)
        assertTrue(states[1] is DetailsUiState.Failed)
    }
}