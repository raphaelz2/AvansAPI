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
 * Protected user routes (JWT required).
 */
fun Route.userRoutes(userRepository: UserRepositoryInterface) {
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

        // Update an existing user by ID
        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val user = call.receive<UpdateUserRequest>()
            userRepository.update(user)
            call.respond(HttpStatusCode.Accepted)
        }

        // Soft delete: disable a user (0 = active, 1 = disabled)
        put("/{id}/disable") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)

            val ok = userRepository.setDisabled(id, 1)
            if (ok) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
        }

        // Optional: re-enable a user
        put("/{id}/enable") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)

            val ok = userRepository.setDisabled(id, 0)
            if (ok) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
        }
    }
}

/**
 * Public registration route (no JWT required).
 * Exposes: POST /users
 */
fun Route.userRegistrationRoutes(userRepository: UserRepositoryInterface) {
    route("/users") {
        post {
            val user = call.receive<CreateUserRequest>()

            // Basic duplicate email protection
            val existing = userRepository.findByEmail(user.email)
            if (existing != null) {
                return@post call.respond(HttpStatusCode.Conflict, "Email already exists")
            }

            val createdUser = userRepository.create(user)
            call.respond(HttpStatusCode.Created, createdUser.toGetUserResponse())
        }
    }
}