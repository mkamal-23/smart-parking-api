package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class ParkingSpace(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val address: String,
    val openingTime: String,  // "09:00"
    val closingTime: String,  // "22:00"
    val isActive: Boolean
)