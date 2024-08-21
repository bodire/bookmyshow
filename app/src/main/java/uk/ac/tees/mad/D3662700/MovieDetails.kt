package uk.ac.tees.mad.D3662700


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.D3662700.data.Movie
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import java.util.*

class MovieDetails : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movie = intent.getSerializableExtra("movie") as? Movie
        if (movie == null) {
            finish()
            return
        }

        setContent {
            BookMyShowTheme {
                var showBookingDialog by remember { mutableStateOf(false) }
                var bookingConfirmed by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(movie.name) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        MovieDetailsContent(movie)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showBookingDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Book Now")
                        }
                    }

                    if (showBookingDialog) {
                        BookingDialog(
                            movie = movie,
                            onDismiss = { showBookingDialog = false },
                            onBookingConfirmed = { numTickets ->

                                addBookingToDatabase(movie, numTickets)
                                showBookingDialog = false
                                bookingConfirmed = true
                            }
                        )
                    }
                }

                if (bookingConfirmed) {
                    AlertDialog(
                        onDismissRequest = { bookingConfirmed = false; finish(); startActivity(
                            Intent(this@MovieDetails, Dashboard::class.java)
                        ) },
                        title = { Text("Booking Confirmed") },
                        text = {
                            Column {
                                Text("Booking confirmed for ${movie.name}!")
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Please pay at the counter.")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                bookingConfirmed = false
                                finish()
                                startActivity(Intent(this@MovieDetails, Dashboard::class.java))
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun addBookingToDatabase(movie: Movie, numTickets: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance().reference
            val bookingId = database.child("bookings").push().key
            if (bookingId != null) {
                val booking = hashMapOf(
                    "userId" to user.uid,
                    "id" to movie.id,
                    "title" to movie.name,
                    "numTickets" to numTickets,
                    "bookingDate" to Date().time,
                    "image" to movie.imageUrl,
                    "lat" to movie.lat,
                    "lng" to movie.lng,
                    "isEvent" to false
                )
                database.child("bookings").child(bookingId).setValue(booking)
            }
        }
    }
}

@Composable
fun MovieDetailsContent(movie: Movie) {
    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(model = movie.imageUrl),
            contentDescription = movie.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(movie.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        MovieInfoRow(Icons.Filled.Category, movie.genre.get(1))
        MovieInfoRow(Icons.Filled.DateRange, movie.releaseDate)
        MovieInfoRow(Icons.Filled.Schedule, " ${movie.duration}")
        MovieInfoRow(Icons.Filled.Place, movie.locationsAvailable.get(1))
        MovieInfoRow(Icons.Filled.Person, movie.director)
        Spacer(modifier = Modifier.height(16.dp))
        Text(movie.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MovieInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BookingDialog(
    movie: Movie,
    onDismiss: () -> Unit,
    onBookingConfirmed: (Int) -> Unit
) {
    var numTickets by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Movie") },
        text = {
            Column {
                Text("How many tickets would you like to book for ${movie.name}?")
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { if (numTickets > 1) numTickets-- }) {
                        Text("-")
                    }
                    Text(
                        text = numTickets.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Button(onClick = { numTickets++ }) {
                        Text("+")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total Price: $${movie.price * numTickets}")
            }
        },
        confirmButton = {
            Button(onClick = { onBookingConfirmed(numTickets) }) {
                Text("Confirm Booking")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}