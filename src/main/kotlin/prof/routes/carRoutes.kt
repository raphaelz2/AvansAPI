package prof.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.mapperExtentions.toGetCarResponse
import prof.mapperExtentions.toGetCarsResponse
import java.io.File

fun Route.carRoutes(carRepository: CarRepository) {
    route("/cars") {
        // Get all cars
        get {
            val cars = carRepository.findAll()
            call.respond(HttpStatusCode.OK, cars.toGetCarsResponse())
        }

        // Get a car by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val car = carRepository.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(HttpStatusCode.OK, car.toGetCarResponse())
        }

        // Create a new car
        post {
            val car = call.receive<CreateCarRequest>()
            val createdCar = carRepository.create(car)
            call.respond(HttpStatusCode.Created, createdCar.toGetCarResponse())
        }

        // Update an existing car by ID
        put("/{id}") {
            call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
            val car = call.receive<UpdateCarRequest>()
            carRepository.update(car)
            call.respond(HttpStatusCode.Accepted)
        }

        // Delete a car by ID
        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = carRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent) // Successfully deleted
            } else {
                call.respond(HttpStatusCode.NotFound) // Car not found
            }
        }

        staticFiles(
            remotePath = "/{id}/images",
            dir = File("uploads"),
        ) {
            default("incident.png")
            extensions("jpg", "png", "jpeg", "gif")
        }

        post("/{id}/images") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid car ID")
            val car = carRepository.findById(id)
                ?: return@post call.respond(HttpStatusCode.NotFound, "Car not found")
            var imageFileName = ""

            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        imageFileName = part.originalFileName ?: "NoImageName"
                        val fileBytes = part.provider().toByteArray()
                        File(getImageUploadPath(imageFileName)).writeBytes(fileBytes) // Save the file
                    }
                    else -> {}
                }
                part.dispose() // Dispose of the part after processing
            }
            carRepository.addImageFileName(id, imageFileName)

            // Respond with success message
            call.respond(
                HttpStatusCode.OK,
                "Image $imageFileName uploaded and associated with car ID $id"
            )
        }
    }
}
private fun getImageUploadPath(imageFile: String) = "uploads/$imageFile"