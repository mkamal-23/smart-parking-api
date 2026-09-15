package com.parking.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtService {
    private val secret = "parking_secret_key"
    private val issuer = "parking-api"
    val realm = "parking"

    fun generateToken(id: Int, role: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("id", id)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000))
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifier() = JWT.require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .build()
}