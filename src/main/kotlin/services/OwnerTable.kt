package com.parking.services

import org.jetbrains.exposed.sql.Table

object OwnerTable : Table("owners") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 200).uniqueIndex()
    val phone = varchar("phone", 15)
    val passwordHash = varchar("password_hash", 255)
    val accountNumber = varchar("account_number", 50)
    val ifscCode = varchar("ifsc_code", 20)
    val accountHolderName = varchar("account_holder_name", 100)
    override val primaryKey = PrimaryKey(id)
}