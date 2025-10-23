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

        // Create a new user
        post {
            val user = call.receive<CreateUserRequest>()
            val createdUser = userRepository.create(user)
            call.respond(HttpStatusCode.Created, createdUser.toGetUserResponse())
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