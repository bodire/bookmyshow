package uk.ac.tees.mad.D3662700



import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import uk.ac.tees.mad.D3662700.data.Event
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.window.Dialog


class Events : ComponentActivity() {

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
                var events by remember { mutableStateOf<List<Event>>(emptyList()) }
                var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
                var showSearchPopup by remember { mutableStateOf(false) }
                var currentCity by remember { mutableStateOf(location) }
                var searchQuery by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    fetchEvents { eventsList ->
                        events = eventsList
                    }
                }

                val availableCategories = events.map { it.category }.distinct().sorted()

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
                                selected = true,
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
                ) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        item {
                            CategoryFilter(
                                categories = availableCategories,
                                selectedCategories = selectedCategories,
                                onCategorySelected = { category ->
                                    selectedCategories = if (selectedCategories.contains(category)) {
                                        selectedCategories - category
                                    } else {
                                        selectedCategories + category
                                    }
                                }
                            )
                        }

                        items(events.filter { event ->
                            selectedCategories.isEmpty() || event.category in selectedCategories
                        }) { event ->
                            EventCard2(
                                event = event,
                                onClick = {
                                    val intent = Intent(this@Events, EventDetails::class.java)
                                    intent.putExtra("event", event)
                                    startActivity(intent)
                                }
                            )
                        }
                    }

                    if (showSearchPopup) {
                        SearchEvent(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onDismiss = { showSearchPopup = false },
                            events = events
                        )
                    }
                }
            }
        }
    }


    private fun fetchEvents(callback: (List<Event>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("events_data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                callback(events)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category in selectedCategories,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
@Composable
fun EventCard2(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(model = event.imageUrl),
                contentDescription = event.name,
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
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.category,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: ${event.startDate}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Venue: ${event.venue}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEvent(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    events: List<Event>
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search events") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    val filteredEvents = events.filter { it.name.contains(query, ignoreCase = true) }

                    items(filteredEvents) { event ->
                        Text(event.name, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}