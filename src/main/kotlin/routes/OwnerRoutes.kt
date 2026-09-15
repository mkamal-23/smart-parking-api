package com.parking.routes

import com.parking.models.Owner
import com.parking.services.DatabaseFactory
import com.parking.services.JwtService
import com.parking.services.OwnerTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

fun Route.ownersRoutes() {
    route("/owners") {
        post("/register") {
            val owner = call.receive<Owner>()

            DatabaseFactory.dbQuery {
                OwnerTable.insert {
                    it[name] = owner.name
                    it[email] = owner.email
                    it[phone] = owner.phone
                    it[passwordHash] = BCrypt.hashpw(owner.passwordHash, BCrypt.gensalt())
                    it[accountNumber] = owner.accountNumber
                    it[ifscCode] = owner.ifscCode
                    it[accountHolderName] = owner.accountHolderName
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Owner registered!"))
        }
        get ("/show") {
            val owners = DatabaseFactory.dbQuery {
                OwnerTable.selectAll().map { row ->
                    Owner(
                        id = row[OwnerTable.id],
                        name = row[OwnerTable.name],
                        email = row[OwnerTable.email],
                        phone = row[OwnerTable.phone],
                        passwordHash = row[OwnerTable.passwordHash],
                        accountNumber = row[OwnerTable.accountNumber],
                        ifscCode = row[OwnerTable.ifscCode],
                        accountHolderName = row[OwnerTable.accountHolderName]
                    )
                }
            }
            call.respond(owners)
        }
        post("/login") {
            val owner = call.receive<Owner>()

            val dbOwner = DatabaseFactory.dbQuery {
                OwnerTable.selectAll()
                    .where { OwnerTable.email eq owner.email }
                    .firstOrNull()
            }

            if (dbOwner == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Owner not found!"))
                return@post
            }

            if (!BCrypt.checkpw(owner.passwordHash, dbOwner[OwnerTable.passwordHash])) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Wrong password!"))
                return@post
            }

            val token = JwtService.generateToken(dbOwner[OwnerTable.id], "OWNER")
            call.respond(mapOf("token" to token))
        }
    }
}