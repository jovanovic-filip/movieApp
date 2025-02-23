package com.civonavoj.movieapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.civonavoj.movieapp.BuildConfig
import com.civonavoj.movieapp.viewmodel.DetailsUiState
import com.civonavoj.movieapp.viewmodel.MovieDetails
import com.civonavoj.movieapp.viewmodel.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    navController: NavController,
    viewModel: MovieDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(movieId) { viewModel.fetchMovieDetails(movieId) }
    MovieDetailsContent(uiState, navController)
}

@Composable
fun MovieDetailsContent(uiState: DetailsUiState, navController: NavController) {
    when (uiState) {
        is DetailsUiState.Loading -> {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        }
        is DetailsUiState.Success -> {
            val movie = uiState.movie
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Release Date: ${movie.releaseDate}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(
                        "${BuildConfig.TMDB_IMAGE_BASE_URL}w500${movie.posterUrl}"),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Overview", style = MaterialTheme.typography.headlineSmall)
                Text(text = movie.overview, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Genres: ${movie.genres.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rating: ${movie.rating}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val runtimeHours = "${movie.runtime / 60}h"
                val minutes = movie.runtime % 60
                val runtimeMinutes = if(minutes > 0)  " ${minutes}m" else ""
                Text(
                    text = "Runtime: $runtimeHours$runtimeMinutes",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Language: ${movie.language}", style = MaterialTheme.typography.bodyMedium)
            }
        }
        is DetailsUiState.Failed -> {
            Text(text = "Failed to load movie details", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewMovieDetailsScreen() {
    MovieDetailsContent(
        uiState = DetailsUiState.Success(
            movie = MovieDetails(
                id = 1,
                title = "Sample Movie",
                releaseDate = "2023-01-01",
                posterUrl = "https://via.placeholder.com/300",
                overview = "This is a sample movie overview.",
                genres = listOf("Action", "Adventure"),
                rating = 4.5,
                runtime = 12,
                language = "English"
            )
        ),
        navController = rememberNavController()
    )
}