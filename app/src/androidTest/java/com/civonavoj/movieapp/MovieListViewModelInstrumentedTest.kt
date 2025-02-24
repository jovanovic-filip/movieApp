package com.civonavoj.movieapp

import androidx.paging.PagingData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.civonavoj.movieapp.api.Movie
import com.civonavoj.movieapp.viewmodel.MovieListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MovieListViewModelInstrumentedTest {
    private lateinit var viewModel: MovieListViewModel
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieListViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun popularMovies_shouldReturnPagingData() = runTest {
        // When
        val pagingData = viewModel.popularMovies.first()
        // Then
        assertNotNull("PagingData should not be null", pagingData)
    }

    @Test
    fun searchMovies_withValidQuery_shouldReturnPagingData() = runTest {
        // Given
        val query = "Matrix"
        // When
        viewModel.searchMovies(query, context)
        val pagingData: PagingData<Movie> = viewModel.searchResults.first()
        // Then
        assertNotNull("Search PagingData should not be null", pagingData)
    }

    @Test
    fun searchMovies_withEmptyQuery_shouldReturnEmpty() = runTest {
        // Given
        val emptyQuery = ""
        // When
        viewModel.searchMovies(emptyQuery, context)
        val pagingData = viewModel.searchResults.first()
        // Then
        assertNotNull("Empty search should return empty PagingData", pagingData)
    }
}
