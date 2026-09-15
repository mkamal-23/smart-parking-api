// BookingRoutes.kt
package com.parking.routes

import com.parking.models.Booking
import com.parking.services.PaymentTable
import com.parking.services.BookingTable
import com.parking.services.DatabaseFactory
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.bookingRoutes() {
    route("/bookings") {
        post("/create") {
            val booking = call.receive<Booking>()
            DatabaseFactory.dbQuery {
                BookingTable.insert {
                    it[userId] = booking.userId
                    it[slotTypeId] = booking.slotTypeId
                    it[startTime] = booking.startTime
                    it[endTime] = booking.endTime
                    it[bookingType] = booking.bookingType
                    it[status] = booking.status
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Booking created!"))
        }

        get("/show") {
            val bookings = DatabaseFactory.dbQuery {
                BookingTable.selectAll().map { row ->
                    Booking(
                        id = row[BookingTable.id],
                        userId = row[BookingTable.userId],
                        slotTypeId = row[BookingTable.slotTypeId],
                        startTime = row[BookingTable.startTime],
                        endTime = row[BookingTable.endTime],
                        bookingType = row[BookingTable.bookingType],
                        status = row[BookingTable.status]
                    )
                }
            }
            call.respond(bookings)
        }
        delete("/{id}/cancel") {
            val bookingId = call.parameters["id"]!!.toInt()
            val now = System.currentTimeMillis()

            val booking = DatabaseFactory.dbQuery {
                BookingTable.selectAll()
                    .where { BookingTable.id eq bookingId }
                    .firstOrNull()
            }

            if (booking == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Booking not found!"))
                return@delete
            }

            val startTime = booking[BookingTable.startTime]
            val payment = DatabaseFactory.dbQuery {
                PaymentTable.selectAll()
                    .where { PaymentTable.bookingId eq bookingId }
                    .firstOrNull()
            }

            val totalAmount = payment?.get(PaymentTable.amount)?.toDouble() ?: 0.0
            val refundAmount: Double

            if (now < startTime) {
                // Pehle cancel kiya — full refund
                refundAmount = totalAmount
                DatabaseFactory.dbQuery {
                    PaymentTable.update({ PaymentTable.bookingId eq bookingId }) {
                        it[status] = "REFUNDED"
                    }
                }
            } else {
                // Baad mein cancel kiya — per minute kato
                val duration = booking[BookingTable.endTime] - startTime
                val elapsed = now - startTime
                val refundRatio = 1.0 - (elapsed.toDouble() / duration.toDouble())
                refundAmount = totalAmount * refundRatio.coerceAtLeast(0.0)
                DatabaseFactory.dbQuery {
                    PaymentTable.update({ PaymentTable.bookingId eq bookingId }) {
                        it[status] = "PARTIALLY_REFUNDED"
                    }
                }
            }

            DatabaseFactory.dbQuery {
                BookingTable.update({ BookingTable.id eq bookingId }) {
                    it[status] = "CANCELLED"
                }
            }

            call.respond(mapOf(
                "message" to "Booking cancelled!",
                "refundAmount" to refundAmount
            ))
        }
    }
}