package uk.ac.tees.mad.D3662700

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.D3662700.data.ExploreDetailsViewModel
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme

class ExploreDetails : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieId = intent.getIntExtra("movieId", -1)

        setContent {
            BookMyShowTheme {
                val viewModel: ExploreDetailsViewModel = viewModel()
                val movieDetails by viewModel.movieDetails.collectAsState()

                LaunchedEffect(movieId) {
                    viewModel.getMovieDetails(movieId)
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(movieDetails?.title ?: "Movie Details") },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        movieDetails?.let { movie ->
                            // Backdrop image
                            Image(
                                painter = rememberAsyncImagePainter(movie.backdrop_path),
                                contentDescription = "Movie Backdrop",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                // Title and release date
                                Text(
                                    text = movie.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Released: ${movie.release_date}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Genres
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                ) {
                                    movie.genres.forEach { genre ->
                                        GenreChip(
                                            text = genre.name,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Overview
                                Text(
                                    text = "Overview",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = movie.overview,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Rating and votes
                                Text(
                                    text = "Rating: ${movie.vote_average}/10 (${movie.vote_count} votes)",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Additional information
                                InfoItem("Runtime", "${movie.runtime} minutes")
                                InfoItem("Budget", "$${movie.budget}")
                                InfoItem("Revenue", "$${movie.revenue}")
                                InfoItem("Status", movie.status)
                                InfoItem("Original Language", movie.original_language)

                                Spacer(modifier = Modifier.height(16.dp))

                                // Production companies
                                Text(
                                    text = "Production Companies",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                movie.production_companies.forEach { company ->
                                    Text(
                                        text = "• ${company.name}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Homepage link
                                Button(
                                    onClick = { /* Open website */ },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Visit Official Website")
                                }
                            }
                        } ?: run {
                            Text(
                                "Loading movie details...",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}