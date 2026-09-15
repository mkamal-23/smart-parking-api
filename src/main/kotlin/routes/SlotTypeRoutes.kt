
package com.parking.routes

import com.parking.models.SlotType
import com.parking.services.DatabaseFactory
import com.parking.services.SlotTypeTable
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.slotTypeRoutes() {
    route("/slots") {
        post("/create") {
            val slot = call.receive<SlotType>()
            DatabaseFactory.dbQuery {
                SlotTypeTable.insert {
                    it[parkingSpaceId] = slot.parkingSpaceId
                    it[type] = slot.type
                    it[totalSlots] = slot.totalSlots
                    it[pricePerMinute] = slot.pricePerMinute.toBigDecimal()
                    it[minDuration] = slot.minDuration
                    it[maxDuration] = slot.maxDuration
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Slot created!"))
        }

        get("/show") {
            val slots = DatabaseFactory.dbQuery {
                SlotTypeTable.selectAll().map { row ->
                    SlotType(
                        id = row[SlotTypeTable.id],
                        parkingSpaceId = row[SlotTypeTable.parkingSpaceId],
                        type = row[SlotTypeTable.type],
                        totalSlots = row[SlotTypeTable.totalSlots],
                        pricePerMinute = row[SlotTypeTable.pricePerMinute].toDouble(),
                        minDuration = row[SlotTypeTable.minDuration],
                        maxDuration = row[SlotTypeTable.maxDuration]
                    )
                }
            }
            call.respond(slots)
        }
    }
}