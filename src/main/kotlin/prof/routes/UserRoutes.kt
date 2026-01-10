package prof.routes

import prof.db.UserRepositoryInterface
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.mapperExtentions.toGetUserResponse
import prof.mapperExtentions.toGetUserResponseList

/**
 * Authenticated user management routes.
 *
 * Note: In many apps, registration (POST /users) should be public. To support that,
 * you can set [includeCreate] to false here and expose a separate public route.
 */
fun Route.userRoutes(userRepository: UserRepositoryInterface, includeCreate: Boolean = true) {
    route("/users") {
        // Get all users
        get {
            val users = userRepository.findAll()
            call.respond(HttpStatusCode.OK, users.toGetUserResponseList())
        }

        // Get a user by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val user = userRepository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, user.toGetUserResponse())
        }

        if (includeCreate) {
            // Create a new user
            post {
                val user = call.receive<CreateUserRequest>()

                // Basic validation + conflict check
                if (user.email.isBlank() || user.password.isBlank() || user.firstName.isBlank()) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Missing required fields")
                }

                val existing = userRepository.findByEmail(user.email)
                if (existing != null) {
                    return@post call.respond(HttpStatusCode.Conflict, "Email already exists")
                }

                val createdUser = userRepository.create(user)
                call.respond(HttpStatusCode.Created, createdUser.toGetUserResponse())
            }
        }

        // Update an existing user by ID
        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val user = call.receive<UpdateUserRequest>()
            userRepository.update(user)
            call.respond(HttpStatusCode.Accepted)
        }

        // Delete a user by ID
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = userRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent) // Successfully deleted
            } else {
                call.respond(HttpStatusCode.NotFound) // User not found
            }
        }
    }
}

/**
 * Public registration route.
 *
 * Exposes POST /users without JWT auth so mobile apps can register.
 */
fun Route.userRegistrationRoutes(userRepository: UserRepositoryInterface) {
    route("/users") {
        post {
            val user = call.receive<CreateUserRequest>()

            if (user.email.isBlank() || user.password.isBlank() || user.firstName.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "Missing required fields")
            }

            val existing = userRepository.findByEmail(user.email)
            if (existing != null) {
                return@post call.respond(HttpStatusCode.Conflict, "Email already exists")
            }

            val createdUser = userRepository.create(user)
            call.respond(HttpStatusCode.Created, createdUser.toGetUserResponse())
        }
    }
}