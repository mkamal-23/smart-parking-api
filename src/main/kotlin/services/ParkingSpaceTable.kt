package com.parking.services

import org.jetbrains.exposed.sql.Table

object ParkingSpaceTable : Table("parking_spaces") {
    val id = integer("id").autoIncrement()
    val ownerId = integer("owner_id").references(OwnerTable.id)
    val name = varchar("name", 200)
    val address = text("address")
    val openingTime = varchar("opening_time", 10)
    val closingTime = varchar("closing_time", 10)
    val isActive = bool("is_active").default(true)
    override val primaryKey = PrimaryKey(id)
}