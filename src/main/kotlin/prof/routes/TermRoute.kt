package prof.routes

import com.sun.security.auth.UserPrincipal
import io.ktor.http.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import prof.AuthenticatedUser
import prof.Requests.CreateTermRequest
import prof.Requests.UpdateTermRequest
import prof.db.TermRepositoryInterface
import prof.mapperExtentions.toGetTermResponse
import prof.mapperExtentions.toGetTermResponseList

fun Route.TermRoute(TermRepository: TermRepositoryInterface) {
    route("/terms") {
        get {
            val terms = TermRepository.findAll()
            call.respond(HttpStatusCode.OK, terms.toGetTermResponseList())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val term = TermRepository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, term.toGetTermResponse())
        }

        post {
            val term = try {
                call.receive<CreateTermRequest>()
            } catch (e: Exception) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Invalid JSON")
                )
            }

            val user = call.principal<AuthenticatedUser>()

            println("Current user ID: $user")
//
//            val createdUser = TermRepository.create(term)
//            call.respond(HttpStatusCode.Created, createdUser.toGetUserResponse())
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            val term = call.receive<UpdateTermRequest>()
            TermRepository.update(term)
            call.respond(HttpStatusCode.Accepted)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = TermRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}