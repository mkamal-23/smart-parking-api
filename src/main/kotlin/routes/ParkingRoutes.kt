package com.parking.routes

import com.parking.models.ParkingSpace
import com.parking.services.DatabaseFactory
import com.parking.services.ParkingSpaceTable
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import com.parking.services.SlotTypeTable
import com.parking.services.BookingTable

fun Route.parkingRoutes() {
    route("/parking") {
        post("/create") {
            val parking = call.receive<ParkingSpace>()
            DatabaseFactory.dbQuery {
                ParkingSpaceTable.insert {
                    it[ownerId] = parking.ownerId
                    it[name] = parking.name
                    it[address] = parking.address
                    it[openingTime] = parking.openingTime
                    it[closingTime] = parking.closingTime
                    it[isActive] = parking.isActive
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Parking space created!"))
        }

        get("/show") {
            val spaces = DatabaseFactory.dbQuery {
                ParkingSpaceTable.selectAll().map { row ->
                    ParkingSpace(
                        id = row[ParkingSpaceTable.id],
                        ownerId = row[ParkingSpaceTable.ownerId],
                        name = row[ParkingSpaceTable.name],
                        address = row[ParkingSpaceTable.address],
                        openingTime = row[ParkingSpaceTable.openingTime],
                        closingTime = row[ParkingSpaceTable.closingTime],
                        isActive = row[ParkingSpaceTable.isActive]
                    )
                }
            }
            call.respond(spaces)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val space = DatabaseFactory.dbQuery {
                ParkingSpaceTable.selectAll()
                    .where { ParkingSpaceTable.id eq id }
                    .map { row ->
                        ParkingSpace(
                            id = row[ParkingSpaceTable.id],
                            ownerId = row[ParkingSpaceTable.ownerId],
                            name = row[ParkingSpaceTable.name],
                            address = row[ParkingSpaceTable.address],
                            openingTime = row[ParkingSpaceTable.openingTime],
                            closingTime = row[ParkingSpaceTable.closingTime],
                            isActive = row[ParkingSpaceTable.isActive]
                        )
                    }.firstOrNull()
            }
            if (space == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Parking space not found!"))
            } else {
                call.respond(space)
            }
        }
        get("/{id}/availability") {
            val parkingId = call.parameters["id"]!!.toInt()
            val now = System.currentTimeMillis()

            val availability = DatabaseFactory.dbQuery {
                SlotTypeTable.selectAll()
                    .where { SlotTypeTable.parkingSpaceId eq parkingId }
                    .map { slotRow ->
                        val slotTypeId = slotRow[SlotTypeTable.id]
                        val totalSlots = slotRow[SlotTypeTable.totalSlots]
                        val type = slotRow[SlotTypeTable.type]

                        val activeBookings = BookingTable.selectAll()
                            .where {
                                (BookingTable.slotTypeId eq slotTypeId) and
                                        (BookingTable.status eq "ACTIVE") and
                                        (BookingTable.endTime greater now)
                            }.count()

                        val availableSlots = totalSlots - activeBookings

                        val nextAvailable = BookingTable
                            .select(BookingTable.endTime.min())
                            .where {
                                (BookingTable.slotTypeId eq slotTypeId) and
                                        (BookingTable.status eq "ACTIVE") and
                                        (BookingTable.endTime greater now)
                            }.firstOrNull()?.get(BookingTable.endTime.min())

                        mapOf(
                            "type" to type,
                            "totalSlots" to totalSlots,
                            "availableSlots" to availableSlots,
                            "nextAvailableAt" to nextAvailable
                        )
                    }
            }
            call.respond(availability)
        }
        get("/search") {
            val query = call.request.queryParameters["name"] ?: ""

            val results = DatabaseFactory.dbQuery {
                ParkingSpaceTable.selectAll()
                    .where { ParkingSpaceTable.name like "%$query%" }
                    .map { row ->
                        ParkingSpace(
                            id = row[ParkingSpaceTable.id],
                            ownerId = row[ParkingSpaceTable.ownerId],
                            name = row[ParkingSpaceTable.name],
                            address = row[ParkingSpaceTable.address],
                            openingTime = row[ParkingSpaceTable.openingTime],
                            closingTime = row[ParkingSpaceTable.closingTime],
                            isActive = row[ParkingSpaceTable.isActive]
                        )
                    }
            }
            call.respond(results)
        }
    }
}