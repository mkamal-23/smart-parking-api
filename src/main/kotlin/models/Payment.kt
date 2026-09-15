package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: Int,
    val bookingId: Int,
    val amount: Double,
    val status: String,  // "PAID", "REFUNDED", "PARTIALLY_REFUNDED"
    val paymentType: String  // "UPI", "QR"
)