package com.parking.routes

import com.parking.models.User
import com.parking.services.DatabaseFactory
import com.parking.services.JwtService
import com.parking.services.UserTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt

fun Route.userRoutes() {
    route("/users") {
        post("/register") {
            val user = call.receive<User>()

            DatabaseFactory.dbQuery {
                UserTable.insert {
                    it[name] = user.name
                    it[email] = user.email
                    it[phone] = user.phone
                    it[passwordHash] = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt())
                    it[role] = user.role
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "User registered!"))
        }
        get ("/show") {
            val users = DatabaseFactory.dbQuery {
                UserTable.selectAll().map { row ->
                    User(
                        id = row[UserTable.id],
                        name = row[UserTable.name],
                        email = row[UserTable.email],
                        phone = row[UserTable.phone],
                        passwordHash = row[UserTable.passwordHash],
                        role = row[UserTable.role],
                    )
                }
            }
            call.respond(users)
        }
        post("/login") {
            val user = call.receive<User>()

            val dbUser = DatabaseFactory.dbQuery {
                UserTable.selectAll()
                    .where { UserTable.email eq user.email }
                    .firstOrNull()
            }

            if (dbUser == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "User not found!"))
                return@post
            }

            if (!BCrypt.checkpw(user.passwordHash, dbUser[UserTable.passwordHash])) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Wrong password!"))
                return@post
            }

            val token = JwtService.generateToken(dbUser[UserTable.id], dbUser[UserTable.role])
            call.respond(mapOf("token" to token))
        }
    }
}