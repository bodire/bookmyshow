package uk.ac.tees.mad.D3662700.data


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext


class ExploreViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Mov>>(emptyList())
    val searchResults: StateFlow<List<Mov>> = _searchResults

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val movieList = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://advanced-movie-search.p.rapidapi.com/search/movie?query=$query&page=1")
                        .get()
                        .addHeader("x-rapidapi-key", "ba3d3c59cdmshdeb594ffe544049p1af077jsn62b4e2e5ab05")
                        .addHeader("x-rapidapi-host", "advanced-movie-search.p.rapidapi.com")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val resultsArray = jsonObject.getJSONArray("results")
                        val movies = mutableListOf<Mov>()

                        for (i in 0 until resultsArray.length()) {
                            val movieObject = resultsArray.getJSONObject(i)
                            val movie = Mov(
                                id = movieObject.getInt("id"),
                                title = movieObject.getString("title"),
                                poster_path = movieObject.getString("poster_path"),
                                release_date = movieObject.getString("release_date"),
                                vote_average = movieObject.getDouble("vote_average")
                            )
                            movies.add(movie)
                        }
                        movies
                    } else {
                        emptyList()
                    }
                }
                _searchResults.value = movieList
            } catch (e: Exception) {
                // Handle the exception (e.g., log it or update UI to show an error)
                e.printStackTrace()
                _searchResults.value = emptyList()
            }
        }
    }
}