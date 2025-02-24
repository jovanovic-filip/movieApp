package com.civonavoj.movieapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.civonavoj.movieapp.R
import com.civonavoj.movieapp.viewmodel.MovieListViewModel

@Composable
fun MoviesListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = viewModel()
) {

    var query by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    val currentItems = if (query.isEmpty()) popularMovies else searchResults

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (isSearchExpanded) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.searchMovies(query)
                    },
                    label = { Text(stringResource(R.string.search)) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                query = ""
                                isSearchExpanded = false
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                )
            } else {
                IconButton(
                    onClick = { isSearchExpanded = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }
        }

        when (currentItems.loadState.refresh) {
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.error_loading_movies))
                }
            }

            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Text(
                    text = when {
                        query.isEmpty() -> stringResource(R.string.top_movies)
                        else -> stringResource(R.string.search_results)
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )

                LazyColumn {
                    items(
                        count = currentItems.itemCount,
                        key = { index ->
                            currentItems[index]?.let { "${it.id}_$index" } ?: index.toString()
                        }
                    ) { index ->
                        val movie = currentItems[index]
                        movie?.let {
                            MovieListItem(
                                movie = it,
                                onMovieClick = { navController.navigate("movieDetails/${it.id}") }
                            )
                        }
                    }
                    when (currentItems.loadState.append) {
                        is LoadState.Loading -> item { LoadingItem() }
                        is LoadState.Error -> item {
                            Text(
                                stringResource(R.string.error_loading_more),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMovieSearchScreen() {
    val navController = rememberNavController()
    MoviesListScreen(navController)
}