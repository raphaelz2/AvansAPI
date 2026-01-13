package prof.routes

import io.ktor.http.*
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.Requests.CreateReservationRequest
import prof.db.ReservationRepositoryInterface
import prof.mapperExtentions.toGetReservationResponse
import prof.mapperExtentions.toGetReservationResponseList
import prof.AuthenticatedUser
import prof.enums.ReservationStatusEnum

fun Route.reservationRoutes(reservationRepository: ReservationRepositoryInterface) {
    route("/reservations") {
        // Get all reservations
        get {
            val user = call.principal<AuthenticatedUser>()!!
            val reservations = reservationRepository.findAll(user.id)
            call.respond(HttpStatusCode.OK, reservations.toGetReservationResponseList())
        }

        get("/available-car/{carId}") {
            val carId = call.parameters["carId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid carId")

            val reservations = reservationRepository.findReservationsForCar(carId)
                .filter { it.status == ReservationStatusEnum.CONFIRMED }

            call.respond(HttpStatusCode.OK, reservations.toGetReservationResponseList())
        }
        // Get a reservation by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val reservation = reservationRepository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, reservation.toGetReservationResponse())
        }

        // Create a new reservation
        post {
            val reservation = call.receive<CreateReservationRequest>()

            val canBookOnTime = reservationRepository.canBookOnTime(reservation)

            if(!canBookOnTime) {
                call.respond(HttpStatusCode.BadRequest, "It has already been booked")
            }

            val createdReservation = reservationRepository.create(reservation)
            call.respond(HttpStatusCode.Created, createdReservation.toGetReservationResponse())
        }

        // Delete a reservation by ID
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = reservationRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}