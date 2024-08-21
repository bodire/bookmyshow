package uk.ac.tees.mad.D3662700.data

import java.io.Serializable

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val venue: String = "",
    val organizer: String = "",
    val ticketPrice: Double = 0.0,
    val totalSeats: Int = 0,
    val availableSeats: Int = 0,
    val imageUrl: String = "",
    val lat:Float= 0f,
    val lng:Float= 0f,
    val locationsAvailable: List<String> = listOf(),
    val tags: List<String> = listOf()
) : Serializable
