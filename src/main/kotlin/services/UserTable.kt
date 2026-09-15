package com.parking.services

import org.jetbrains.exposed.sql.Table

object UserTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 200).uniqueIndex()
    val phone = varchar("phone", 15)
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20)
    override val primaryKey = PrimaryKey(id)
}