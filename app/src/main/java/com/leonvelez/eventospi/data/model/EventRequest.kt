package com.leonvelez.eventospi.data.model

data class EventRequest(
    val id: Int = 0,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val maxParticipants: Int,
    val isPublic: Boolean,
    val category: Int,
    val price: Double?,
    val imageUrl: String?
)