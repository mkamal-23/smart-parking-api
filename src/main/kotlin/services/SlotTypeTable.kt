package com.parking.services

import org.jetbrains.exposed.sql.Table

object SlotTypeTable : Table("slot_types") {
    val id = integer("id").autoIncrement()
    val parkingSpaceId = integer("parking_space_id").references(ParkingSpaceTable.id)
    val type = varchar("type", 20)
    val totalSlots = integer("total_slots")
    val pricePerMinute = decimal("price_per_minute", 10, 2)
    val minDuration = integer("min_duration")
    val maxDuration = integer("max_duration")
    override val primaryKey = PrimaryKey(id)
}