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
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.ac.tees.mad.D3662700.data.Event
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import java.util.*

class EventDetails : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = intent.getSerializableExtra("event") as? Event
        if (event == null) {
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
                            title = { Text(event.name) },
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
                        EventDetailsContent(event)
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
                            event = event,
                            onDismiss = { showBookingDialog = false },
                            onBookingConfirmed = { numTickets ->

                                addBookingToDatabase(event, numTickets)
                                showBookingDialog = false
                                bookingConfirmed = true

                            }
                        )
                    }
                }

                if (bookingConfirmed) {
                    AlertDialog(
                        onDismissRequest = { bookingConfirmed = false; finish(); startActivity(Intent(this@EventDetails, Dashboard::class.java)) },
                        title = { Text("Booking Confirmed") },
                        text = {
                            Column {
                                Text("Booking confirmed for ${event.name}!")
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Please pay at the counter.")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                bookingConfirmed = false
                                finish()
                                startActivity(Intent(this@EventDetails, Dashboard::class.java))
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun addBookingToDatabase(event: Event, numTickets: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance().reference
            val bookingId = database.child("bookings").push().key
            if (bookingId != null) {
                val booking = hashMapOf(
                    "userId" to user.uid,
                    "id" to event.id,
                    "title" to event.name,
                    "numTickets" to numTickets,
                    "bookingDate" to Date().time,
                    "image" to event.imageUrl,
                    "lat" to event.lat,
                    "lng" to event.lng,
                    "isEvent" to true
                )
                database.child("bookings").child(bookingId).setValue(booking)
            }
        }
    }
}

@Composable
fun EventDetailsContent(event: Event) {
    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(model = event.imageUrl),
            contentDescription = event.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(event.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        EventInfoRow(Icons.Filled.Category, event.category)
        EventInfoRow(Icons.Filled.DateRange, "${event.startDate} - ${event.endDate}")
        EventInfoRow(Icons.Filled.Schedule, "${event.startTime} - ${event.endTime}")
        EventInfoRow(Icons.Filled.Place, event.venue)
        EventInfoRow(Icons.Filled.Person, event.organizer)
        Spacer(modifier = Modifier.height(16.dp))
        Text(event.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BookingDialog(
    event: Event,
    onDismiss: () -> Unit,
    onBookingConfirmed: (Int) -> Unit
) {
    var numTickets by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Event") },
        text = {
            Column {
                Text("How many tickets would you like to book for ${event.name}?")
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
                Text("Total Price: $${event.ticketPrice * numTickets}")
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
