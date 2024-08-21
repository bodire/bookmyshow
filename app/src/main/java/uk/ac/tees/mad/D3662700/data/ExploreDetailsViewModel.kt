package uk.ac.tees.mad.D3662700.data


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ExploreDetailsViewModel : ViewModel() {
    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails: StateFlow<MovieDetails?> = _movieDetails

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val details = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://advanced-movie-search.p.rapidapi.com/movies/getdetails?movie_id=$movieId")
                        .get()
                        .addHeader("x-rapidapi-key", "ba3d3c59cdmshdeb594ffe544049p1af077jsn62b4e2e5ab05")
                        .addHeader("x-rapidapi-host", "advanced-movie-search.p.rapidapi.com")
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        MovieDetails(
                            id = jsonObject.getInt("id"),
                            title = jsonObject.getString("title"),
                            overview = jsonObject.getString("overview"),
                            poster_path = jsonObject.getString("poster_path"),
                            backdrop_path = jsonObject.getString("backdrop_path"),
                            release_date = jsonObject.getString("release_date"),
                            vote_average = jsonObject.getDouble("vote_average"),
                            vote_count = jsonObject.getInt("vote_count"),
                            runtime = jsonObject.getInt("runtime"),
                            budget = jsonObject.getLong("budget"),
                            revenue = jsonObject.getLong("revenue"),
                            status = jsonObject.getString("status"),
                            original_language = jsonObject.getString("original_language"),
                            genres = jsonObject.getJSONArray("genres").let { genresArray ->
                                List(genresArray.length()) { i ->
                                    val genreObject = genresArray.getJSONObject(i)
                                    MovieDetails.Genre(
                                        id = genreObject.getInt("id"),
                                        name = genreObject.getString("name")
                                    )
                                }
                            },
                            production_companies = jsonObject.getJSONArray("production_companies").let { companiesArray ->
                                List(companiesArray.length()) { i ->
                                    val companyObject = companiesArray.getJSONObject(i)
                                    MovieDetails.ProductionCompany(
                                        id = companyObject.getInt("id"),
                                        name = companyObject.getString("name"),
                                        logo_path = companyObject.optString("logo_path"),
                                        origin_country = companyObject.getString("origin_country")
                                    )
                                }
                            },
                            homepage = jsonObject.getString("homepage")
                        )
                    } else {
                        null
                    }
                }
                _movieDetails.value = details
            } catch (e: Exception) {
                e.printStackTrace()
                _movieDetails.value = null
            }
        }
    }
}