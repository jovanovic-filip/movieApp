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
        // Given
        val movieId = 27205
        
        // When
        val states = viewModel.uiState.take(2).toList()
        viewModel.fetchMovieDetails(movieId)

        // Then
        assertTrue("First state should be Loading", states[0] is DetailsUiState.Loading)
        assertTrue("Second state should be Success", states[1] is DetailsUiState.Success)
        
        val successState = states[1] as DetailsUiState.Success
        assertEquals("Movie ID should match", movieId, successState.movie.id)
    }

    @Test
    fun fetchMovieDetails_withInvalidId_shouldEmitLoadingThenError() = runTest {
        // Given
        val invalidMovieId = -1
        
        // When
        val states = viewModel.uiState.take(2).toList()
        viewModel.fetchMovieDetails(invalidMovieId)

        // Then
        assertTrue("First state should be Loading", states[0] is DetailsUiState.Loading)
        assertTrue("Second state should be Failed", states[1] is DetailsUiState.Failed)
    }
}
