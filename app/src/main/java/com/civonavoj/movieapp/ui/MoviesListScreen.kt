package com.civonavoj.movieapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.civonavoj.movieapp.BuildConfig
import com.civonavoj.movieapp.R
import com.civonavoj.movieapp.viewmodel.MovieListUiState
import com.civonavoj.movieapp.viewmodel.MovieListViewModel

@Composable
fun MoviesListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var isTopRatedOrPopular by remember { mutableStateOf(false) }

    LaunchedEffect(isTopRatedOrPopular) {
        when(isTopRatedOrPopular) {
            false -> viewModel.fetchPopularMovies()
            else -> viewModel.fetchTopRatedMovies()
        }
    }

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
                        viewModel.searchMoviesByKeyword(query)
                    },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                query = ""
                                isSearchExpanded = false
                                focusManager.clearFocus()
                                viewModel.fetchPopularMovies()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
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
                        contentDescription = "Search"
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = when {
                    query.isEmpty() -> when (isTopRatedOrPopular) {
                        true -> "Top Movies"
                        false -> "Popular Movies"
                    }
                    else -> "Search Results"
                },
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { isTopRatedOrPopular = isTopRatedOrPopular.not() },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle popular or top rated movies"
                )
            }
        }


        when (uiState) {
            is MovieListUiState.Loading -> {
                // Optionally, you can show a loading indicator here
            }
            is MovieListUiState.Success -> {
                val movies = (uiState as MovieListUiState.Success).movies
                LazyColumn {
                    items(movies.size) { index ->
                        val movie = movies[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("movieDetails/${movie.id}")
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    "${BuildConfig.TMDB_IMAGE_BASE_URL}w500${movie.posterUrl}"
                                ),
                                contentDescription = "Movie poster for ${movie.title}",
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = movie.title, style = MaterialTheme.typography.bodyLarge)
                                Text(text = movie.releaseDate, style = MaterialTheme.typography.bodySmall)
                                Row {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = ImageVector.vectorResource(
                                                id = when {
                                                    index < movie.rating.toInt() -> R.drawable.ic_star_filled
                                                    else -> R.drawable.ic_star_outline
                                                }
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is MovieListUiState.Failed -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Failed to load movies, tap here to try again.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is MovieListUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No movies found"
                    )
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