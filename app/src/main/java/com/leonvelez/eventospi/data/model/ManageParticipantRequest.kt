package com.leonvelez.eventospi.data.model

data class ManageParticipantRequest(
    val eventId: Int,
    val userId: String,
    val approve: Boolean
)