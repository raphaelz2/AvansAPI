package prof.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import prof.Requests.CreateTelemetryRequest
import prof.db.TelemetryRepositoryInterface

fun Application.telemetryRoutes(repo: TelemetryRepositoryInterface) {
    routing {
        authenticate("auth-jwt") {
            route("/telemetry") {

                post {
                    val req = runCatching { call.receive<CreateTelemetryRequest>() }.getOrNull() ?: CreateTelemetryRequest()
                    val created = repo.create(req, defaultUserId = 1L, defaultCarId = req.carId ?: 1L)
                    call.respond(HttpStatusCode.Created, created)
                }

                get { call.respond(repo.findAll()) }

                get("/{tripId}") {
                    val id = call.parameters["tripId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "tripId required")
                    repo.findByTripId(id)?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
                }

                // examples of filters:
                get("/user/{userId}") {
                    val uid = call.parameters["userId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respond(repo.findForUser(uid))
                }
                get("/car/{carId}") {
                    val cid = call.parameters["carId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)
                    call.respond(repo.findForCar(cid))
                }
                get("/car/{carId}/window") {
                    val cid = call.parameters["carId"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val start = call.request.queryParameters["start"]?.let { LocalDateTime.parse(it) }
                    val end   = call.request.queryParameters["end"]?.let { LocalDateTime.parse(it) }
                    if (start == null || end == null) return@get call.respond(HttpStatusCode.BadRequest, "start & end required")
                    call.respond(repo.findForCarAndTimeframe(cid, start, end))
                }
            }
        }
    }
}
