package com.leonvelez.eventospi.data.model

data class EventParticipantResponse(
    val id: Int,
    val userId: String,
    val userName: String,
    val userFirstName: String,
    val userLastName: String,
    val eventId: Int,
    val event: EventResponse,
    val registrationDate: String,
    val status: Int,
    val confirmationDate: String?,
    val cancellationReason: String?
)