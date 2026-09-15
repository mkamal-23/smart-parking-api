package com.parking

import io.ktor.server.netty.*
import io.ktor.server.engine.*
import io.ktor.server.application.*
import com.parking.services.DatabaseFactory
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.parking.services.JwtService

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){
    DatabaseFactory.init()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtService.realm
            verifier(JwtService.verifier())
            validate { credential ->
                if (credential.payload.getClaim("id").asInt() != null)
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }

    configureHttp()
    configureRouting()
    configureSerialization()
    configureStatusPages()
}