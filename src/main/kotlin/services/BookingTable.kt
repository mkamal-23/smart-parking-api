package com.parking.services

import org.jetbrains.exposed.sql.Table

object BookingTable : Table("bookings") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id)
    val slotTypeId = integer("slot_type_id").references(SlotTypeTable.id)
    val startTime = long("start_time")
    val endTime = long("end_time")
    val bookingType = varchar("booking_type", 20)
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(id)
}