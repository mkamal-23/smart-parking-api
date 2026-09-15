package com.parking.services

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object HolidayScheduleTable : Table("holiday_schedules") {
    val id = integer("id").autoIncrement()
    val parkingSpaceId = integer("parking_space_id").references(ParkingSpaceTable.id)
    val date = date("date")
    val isHoliday = bool("is_holiday").default(true)
    override val primaryKey = PrimaryKey(id)
}