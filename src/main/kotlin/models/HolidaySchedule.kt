package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class HolidaySchedule(
    val id: Int,
    val parkingSpaceId: Int,
    val date: String,     // "2024-01-26"
    val isHoliday: Boolean
)