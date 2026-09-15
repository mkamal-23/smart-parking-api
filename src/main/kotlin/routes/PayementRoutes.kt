
package com.parking.routes

import com.parking.models.Payment
import com.parking.services.DatabaseFactory
import com.parking.services.PaymentTable
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.paymentRoutes() {
    route("/payments") {
        post("/create") {
            val payment = call.receive<Payment>()
            DatabaseFactory.dbQuery {
                PaymentTable.insert {
                    it[bookingId] = payment.bookingId
                    it[amount] = payment.amount.toBigDecimal()
                    it[status] = payment.status
                    it[paymentType] = payment.paymentType
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Payment recorded!"))
        }

        get("/show") {
            val payments = DatabaseFactory.dbQuery {
                PaymentTable.selectAll().map { row ->
                    Payment(
                        id = row[PaymentTable.id],
                        bookingId = row[PaymentTable.bookingId],
                        amount = row[PaymentTable.amount].toDouble(),
                        status = row[PaymentTable.status],
                        paymentType = row[PaymentTable.paymentType]
                    )
                }
            }
            call.respond(payments)
        }
    }
}