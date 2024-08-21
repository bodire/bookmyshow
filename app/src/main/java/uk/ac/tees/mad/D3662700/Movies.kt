package uk.ac.tees.mad.D3662700

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.ac.tees.mad.D3662700.data.Movie
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import uk.ac.tees.mad.D3662700.screens.SearchMovie
import uk.ac.tees.mad.D3662700.screens.SearchPopup

class Movies : ComponentActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = this.getSharedPreferences("mylocation", Context.MODE_PRIVATE)

        val location = sharedPref.getString("city", "")

        setContent {
            BookMyShowTheme {
                var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
                var selectedGenres by remember { mutableStateOf<Set<String>>(emptySet()) }
                var showSearchPopup by remember {mutableStateOf(false)}
                var currentCity by remember { mutableStateOf(location) }
                var searchQuery by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    fetchMovies { moviesList ->
                        movies = moviesList
                    }
                }

                val availableGenres = movies.flatMap { it.genre }.distinct().sorted()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text("Book My Show")
                                    if (location != null) {
                                        Text(
                                            text = location,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = { showSearchPopup = true }) {
                                    Icon(Icons.Filled.Search, contentDescription = "Search")
                                }
                                IconButton(onClick = { /* Implement notifications */ }) {
                                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                                }
                                IconButton(onClick = { /* Implement QR code scanner */ }) {
                                    Icon(Icons.Filled.QrCodeScanner, contentDescription = "QR Code")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        Column {
                            ExploreMoviesBanner(
                                onExploreClick = {
                                    val intent = Intent(this@Movies, Explore::class.java)
                                    startActivity(intent)
                                }
                            )
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = false,
                                    onClick = { intent = Intent(applicationContext,Movies::class.java);
                                        startActivity(intent) }
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Movie,
                                            contentDescription = "Movies"
                                        )
                                    },
                                    label = { Text("Movies") },
                                    selected = true,
                                    onClick = {
                                        intent = Intent(applicationContext, Movies::class.java);
                                        startActivity(intent)
                                    }
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Event,
                                            contentDescription = "Events"
                                        )
                                    },
                                    label = { Text("Events") },
                                    selected = false,
                                    onClick = {
                                        intent = Intent(applicationContext, Events::class.java);
                                        startActivity(intent)
                                    }
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Person,
                                            contentDescription = "Profile"
                                        )
                                    },
                                    label = { Text("Profile") },
                                    selected = false,
                                    onClick = {
                                        intent = Intent(applicationContext, Profile::class.java);
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {


                        item {
                            GenreFilter(
                                genres = availableGenres,
                                selectedGenres = selectedGenres,
                                onGenreSelected = { genre ->
                                    selectedGenres = if (selectedGenres.contains(genre)) {
                                        selectedGenres - genre
                                    } else {
                                        selectedGenres + genre
                                    }
                                }
                            )
                        }

                        items(movies.filter { movie ->
                            selectedGenres.isEmpty() || movie.genre.any { it in selectedGenres }
                        }) { movie ->
                            MovieCard2(movie,
                                onClick = {
                                    val intent = Intent(this@Movies, MovieDetails::class.java)
                                    intent.putExtra("movie", movie)
                                    startActivity(intent)
                                })
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                            }
                        }
                    }
                }
                if (showSearchPopup) {
                    SearchMovie(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onDismiss = { showSearchPopup = false },

                        movies = movies
                    )
                }
            }
        }
    }

    private fun fetchMovies(callback: (List<Movie>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("movies_data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movies = snapshot.children.mapNotNull { it.getValue(Movie::class.java) }
                callback(movies)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreFilter(
    genres: List<String>,
    selectedGenres: Set<String>,
    onGenreSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(genres) { genre ->
            FilterChip(
                selected = genre in selectedGenres,
                onClick = { onGenreSelected(genre) },
                label = { Text(genre) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun MovieCard2(movie: Movie,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = movie.imageUrl),
                contentDescription = movie.name,
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
                    text = movie.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${movie.rating}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${movie.cast.size} votes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.genre.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ExploreMoviesBanner(onExploreClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://example.com/banner_image.jpg"),
                contentDescription = "Explore Movies Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Discover New Movies",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onExploreClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Explore Movies")
                }
            }
        }
    }
}