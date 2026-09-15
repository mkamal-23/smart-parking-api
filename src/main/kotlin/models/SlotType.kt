package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class SlotType(
    val id: Int,
    val parkingSpaceId: Int,
    val type: String,       // "TWO_WHEELER" ya "FOUR_WHEELER"
    val totalSlots: Int,
    val pricePerMinute: Double,
    val minDuration: Int,   // minutes mein
    val maxDuration: Int    // minutes mein
)