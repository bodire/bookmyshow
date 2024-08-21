package uk.ac.tees.mad.D3662700.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.tees.mad.D3662700.data.Event
import uk.ac.tees.mad.D3662700.data.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPopup(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    events: List<Event>,
    movies: List<Movie>
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search events and movies") },
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
                    val filteredMovies = movies.filter { it.name.contains(query, ignoreCase = true) }

                    if (filteredEvents.isNotEmpty()) {
                        item { Text("Events", style = MaterialTheme.typography.titleMedium) }
                        items(filteredEvents) { event ->
                            Text(event.name, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    if (filteredMovies.isNotEmpty()) {
                        item { Text("Movies", style = MaterialTheme.typography.titleMedium) }
                        items(filteredMovies) { movie ->
                            Text(movie.name, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    if (filteredEvents.isEmpty() && filteredMovies.isEmpty() && query.isNotEmpty()) {
                        item { Text("No results found") }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovie(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    movies: List<Movie>
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search events and movies") },
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

                    val filteredMovies = movies.filter { it.name.contains(query, ignoreCase = true) }



                    if (filteredMovies.isNotEmpty()) {
                        item { Text("Movies", style = MaterialTheme.typography.titleMedium) }
                        items(filteredMovies) { movie ->
                            Text(movie.name, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }


                }
            }
        }
    }
}