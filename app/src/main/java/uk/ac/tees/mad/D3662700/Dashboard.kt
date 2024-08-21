package uk.ac.tees.mad.D3662700

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import uk.ac.tees.mad.D3662700.data.Event
import uk.ac.tees.mad.D3662700.data.Movie
import uk.ac.tees.mad.D3662700.screens.AutoScrollingImageCarousel
import uk.ac.tees.mad.D3662700.screens.SearchPopup

class Dashboard : ComponentActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMyShowTheme {
                var currentCity by remember { mutableStateOf("Loading...") }
                var carouselImages by remember { mutableStateOf(listOf<String>()) }
                var events by remember { mutableStateOf(listOf<Event>()) }
                var movies by remember { mutableStateOf(listOf<Movie>()) }
                var showSearchPopup by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    requestLocationPermission()
                    currentCity = getCurrentCity()
                    fetchCarouselImages { images -> carouselImages = images }
                    fetchEvents { eventsList -> events = eventsList }
                    fetchMovies { moviesList -> movies = moviesList }
                }

                    Scaffold(
                        topBar = {
                            Column {
                                TopAppBar(
                                    title = {
                                        Column {
                                            Text("Book My Show")
                                            Text(
                                                text = currentCity,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
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
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = true,
                                    onClick = { intent = Intent(applicationContext,Dashboard::class.java);
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
                                    selected = false,
                                    onClick = { intent = Intent(applicationContext,Movies::class.java);
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
                                    onClick = { intent = Intent(applicationContext,Events::class.java);
                                        startActivity(intent) }
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
                                    onClick = { intent = Intent(applicationContext,Profile::class.java);
                                        startActivity(intent) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {

                            item {
                                AutoScrollingImageCarousel(images = carouselImages)
                            }
                            item {
                                EventsSection(events = events)
                            }
                            item {
                                MoviesSection(movies = movies)
                            }
                        }

                        if (showSearchPopup) {
                            SearchPopup(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onDismiss = { showSearchPopup = false },
                                events = events,
                                movies = movies
                            )
                        }
                    }
                }
            }
        }




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getCurrentCity(): String {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                try {
                    val location = fusedLocationClient.lastLocation.await() ?: return "Unknown City"
                    val geocoder = Geocoder(this, Locale.getDefault())


                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if(null != addresses?.firstOrNull()?.locality) {
                            val sharedPref = applicationContext.getSharedPreferences("mylocation", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("city", addresses?.firstOrNull()?.locality)

                                apply()
                            }
                        }

                        return addresses?.firstOrNull()?.locality ?: "Unknown City"


                    "Unknown City"
                } catch (e: Exception) {
                    "Unknown City"
                }
            }


            else -> {""}
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array< String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, you can get the location now
                    // You might want to refresh the UI or recall getCurrentCity() here
                } else {
                    // Permission denied, handle the failure scenario
                    // Maybe show a message to the user explaining why the location is important
                }
                return
            }
        }
    }

    private fun fetchCarouselImages(callback: (List<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("carousel_data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                if (images.isNotEmpty()) {
                    callback(images)
                } else {
                    callback(listOf("https://example.com/placeholder.jpg"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(listOf("https://example.com/placeholder.jpg"))
            }
        })
    }

    private fun fetchEvents(callback: (List<Event>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("events_data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                    callback(events)
                } catch (e: Exception) {

                    Toast.makeText(this@Dashboard, "Error fetching events", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@Dashboard, "Error fetching events", Toast.LENGTH_SHORT).show()
                callback(emptyList())
            }
        })
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



@Composable
fun EventsSection(events: List<Event>) {
    Column {
        Text(
            text = "Events",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        LazyRow {
            items(events) { event ->
                EventCard(event)
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = event.imageUrl),
                contentDescription = event.name,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MoviesSection(movies: List<Movie>) {
    Column {
        Text(
            text = "Movies",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        LazyRow {
            items(movies) { movie ->
                MovieCard(movie)
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = movie.imageUrl),
                contentDescription = movie.name,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}




