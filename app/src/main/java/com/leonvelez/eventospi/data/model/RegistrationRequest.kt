package com.leonvelez.eventospi.data.model

data class RegistrationRequest(
    val eventId: Int,
    val cancellationReason: String = ""
)