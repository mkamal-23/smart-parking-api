package com.parking.services

import org.jetbrains.exposed.sql.Table

object PaymentTable : Table("payments") {
    val id = integer("id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingTable.id)
    val amount = decimal("amount", 10, 2)
    val status = varchar("status", 20)
    val paymentType = varchar("payment_type", 20)
    override val primaryKey = PrimaryKey(id)
}