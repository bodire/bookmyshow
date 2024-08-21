package uk.ac.tees.mad.D3662700.data

import java.io.Serializable

data class Movie(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val description: String = "",
    val genre: List<String> = listOf(),
    val duration: Int = 0,
    val releaseDate: String = "",
    val language: String = "",
    val cast: List<String> = listOf(),
    val director: String = "",
    val rating: Double = 0.0,
    val imageUrl: String = "",
    val trailerUrl: String = "",
    val lat:Float= 0f,
    val lng:Float= 0f,
    val locationsAvailable: List<String> = listOf()
) : Serializable
