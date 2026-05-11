package com.leonvelez.eventospi.data.model

data class EventResponse(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val maxParticipants: Int,
    val isPublic: Boolean,
    val imageUrl: String?,
    val price: Double?,
    val category: Int,
    val createdByUserId: String?,
    val createdByUserName: String
)