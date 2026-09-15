package com.parking.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String // "CUSTOMER" ya "OWNER"
)