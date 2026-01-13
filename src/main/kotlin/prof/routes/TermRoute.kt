package prof.routes

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
         get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val term = TermRepository.getActive(userId)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, term.toGetTermResponse())
        }
        
        get {
            val user = call.principal<AuthenticatedUser>()!!
            val terms = TermRepository.findAll(user.id)

            call.respond(HttpStatusCode.OK, terms.toGetTermResponseList())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val user = call.principal<AuthenticatedUser>()!!

            val term = TermRepository.findById(id, user.id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, term.toGetTermResponse())
        }

        post {
            val term = call.receive<CreateTermRequest>()
            val user = call.principal<AuthenticatedUser>()

            if (user != null) {
                val createdTerm = TermRepository.create(term, user.id)
                call.respond(HttpStatusCode.Created, createdTerm.toGetTermResponse())
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
            }
        }

        put {
            val term = call.receive<UpdateTermRequest>()
            val user = call.principal<AuthenticatedUser>()!!

            TermRepository.update(term, user.id)
            call.respond(HttpStatusCode.Accepted)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val user = call.principal<AuthenticatedUser>()!!

            val deleted = TermRepository.delete(id, user.id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}