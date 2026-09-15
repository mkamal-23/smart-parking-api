package com.parking

import com.parking.routes.bookingRoutes
import com.parking.routes.holidayRoutes
import com.parking.routes.ownersRoutes
import com.parking.routes.parkingRoutes
import com.parking.routes.paymentRoutes
import com.parking.routes.slotTypeRoutes
import com.parking.routes.userRoutes
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRoutes()
        ownersRoutes()
        authenticate("auth-jwt") {
            parkingRoutes()
            bookingRoutes()
            holidayRoutes()
            slotTypeRoutes()
            paymentRoutes()
        }
        get("/great") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
        get("/kapil") {
            call.respond("Kapil gadha, Kamal bhaiya goat")
        }
        get("/kamal") {
                val text = "<h1>Hello From Ktor</h1>"
                val type = ContentType.parse("text/html")
                call.respondText(text, type)
        }
    }
}

