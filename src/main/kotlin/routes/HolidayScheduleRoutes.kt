// HolidayScheduleRoutes.kt
package com.parking.routes

import com.parking.models.HolidaySchedule
import com.parking.services.DatabaseFactory
import com.parking.services.HolidayScheduleTable
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Route.holidayRoutes() {
    route("/holidays") {
        post("/create") {
            val holiday = call.receive<HolidaySchedule>()
            DatabaseFactory.dbQuery {
                HolidayScheduleTable.insert {
                    it[parkingSpaceId] = holiday.parkingSpaceId
                    it[date] = java.time.LocalDate.parse(holiday.date)
                    it[isHoliday] = holiday.isHoliday
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Holiday added!"))
        }

        get("/show") {
            val holidays = DatabaseFactory.dbQuery {
                HolidayScheduleTable.selectAll().map { row ->
                    HolidaySchedule(
                        id = row[HolidayScheduleTable.id],
                        parkingSpaceId = row[HolidayScheduleTable.parkingSpaceId],
                        date = row[HolidayScheduleTable.date].toString(),
                        isHoliday = row[HolidayScheduleTable.isHoliday]
                    )
                }
            }
            call.respond(holidays)
        }
    }
}