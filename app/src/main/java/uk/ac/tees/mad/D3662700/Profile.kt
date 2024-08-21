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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.D3662700.data.Event
import uk.ac.tees.mad.D3662700.data.Movie
import uk.ac.tees.mad.D3662700.screens.AutoScrollingImageCarousel
import uk.ac.tees.mad.D3662700.screens.SearchPopup

class Profile : ComponentActivity() {

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
                                selected = true,
                                onClick = { intent = Intent(applicationContext,Profile::class.java);
                                    startActivity(intent) }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        ProfileHeader()
                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileButton(
                            icon = Icons.Filled.List,
                            text = "Bookings",
                            onClick = { intent = Intent(applicationContext,Bookings::class.java)
                            startActivity(intent)
                            }
                        )
                        ProfileButton(
                            icon = Icons.Filled.Report,
                            text = "Report Issue",
                            onClick = { intent = Intent(applicationContext,ReportIssueActivityForUserReports::class.java)
                                startActivity(intent) }
                        )
                        ProfileButton(
                            icon = Icons.Filled.Logout,
                            text = "Logout",
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                intent = Intent(applicationContext, SignIn::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "user@example.com"

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "User Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = userEmail,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ProfileButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        }
    }
}
