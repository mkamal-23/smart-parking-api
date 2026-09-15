package com.parking.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DatabaseFactory {

    private lateinit var db: Database

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: ""
            username = System.getenv("DB_USERNAME") ?: ""
            password = System.getenv("DB_PASSWORD") ?: ""
            maximumPoolSize = 3
        }
        db = Database.connect(HikariDataSource(config))
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = db) { block() }
}