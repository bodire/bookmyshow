package uk.ac.tees.mad.D3662700

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import uk.ac.tees.mad.D3662700.data.ExploreViewModel
import uk.ac.tees.mad.D3662700.data.Mov
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme


class Explore : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMyShowTheme {
                val viewModel: ExploreViewModel = viewModel()
                val searchResults by viewModel.searchResults.collectAsState()
                var searchQuery by remember { mutableStateOf("") }
                val coroutineScope = rememberCoroutineScope()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopAppBar(
                            title = { Text("Explore Movies") },
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Search movies") },
                                modifier = Modifier
                                    .weight(0.7f)
                                    .padding(end = 8.dp)
                            )
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.searchMovies(searchQuery)
                                    }
                                },
                                modifier = Modifier
                                    .weight(0.3f)
                                    .height(56.dp) // Match the height of the TextField
                            ) {
                                Text("Search")
                            }
                        }

                        LazyColumn {
                            items(searchResults) { movie ->
                                MovieCard(movie) {
                                    val intent = Intent(this@Explore, ExploreDetails::class.java)
                                    intent.putExtra("movieId", movie.id)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Mov, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = movie.poster_path,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Release Date: ${movie.release_date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Rating: ${movie.vote_average}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}