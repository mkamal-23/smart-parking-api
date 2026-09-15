package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: Int,
    val userId: Int,
    val slotTypeId: Int,
    val startTime: Long,    // epoch timestamp
    val endTime: Long,
    val bookingType: String, // "PRE_BOOK" ya "WALK_IN_ONLINE"
    val status: String      // "ACTIVE", "CANCELLED", "COMPLETED"
)
