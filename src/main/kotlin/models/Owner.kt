package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val accountNumber: String,
    val ifscCode: String,
    val accountHolderName: String
)