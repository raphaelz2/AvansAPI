package prof.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.enums.PowerSourceTypeEnum
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
        post("/create") {
            val car = call.receive<CreateCarRequest>()
            val createdCar = carRepository.create(car)
            call.respond(HttpStatusCode.Created, createdCar.toGetCarResponse())
        }

        get("/search") {
            val filter = CarSearchFilterRequest(
                latitude = call.parameters["latitude"]?.toDoubleOrNull(),
                longitude = call.parameters["longitude"]?.toDoubleOrNull(),
                maxDistanceKm = call.parameters["maxDistanceKm"]?.toDoubleOrNull(),

                make = call.parameters["make"],
                model = call.parameters["model"],
                powerSourceType = call.parameters["powerSourceType"]?.let {
                    try {
                        PowerSourceTypeEnum.valueOf(it.uppercase()) } catch (e: Exception) { null }
                },
                category = call.parameters["category"],
                fuelType = call.parameters["fuelType"],
                transmission = call.parameters["transmission"],
                color = call.parameters["color"],
                interiorColor = call.parameters["interiorColor"],
                exteriorType = call.parameters["exteriorType"],

                minPrice = call.parameters["minPrice"]?.toDoubleOrNull(),
                maxPrice = call.parameters["maxPrice"]?.toDoubleOrNull(),
                minSeats = call.parameters["minSeats"]?.toIntOrNull(),
                maxSeats = call.parameters["maxSeats"]?.toIntOrNull(),
                minDoors = call.parameters["minDoors"]?.toIntOrNull(),
                maxDoors = call.parameters["maxDoors"]?.toIntOrNull(),
                minModelYear = call.parameters["minModelYear"]?.toIntOrNull(),
                maxModelYear = call.parameters["maxModelYear"]?.toIntOrNull(),
                minMileage = call.parameters["minMileage"]?.toIntOrNull(),
                maxMileage = call.parameters["maxMileage"]?.toIntOrNull(),

                searchQuery = call.parameters["q"]
            )

            val cars = carRepository.search(filter)
            call.respond(HttpStatusCode.OK, cars.toGetCarsResponse())
        }

        post("/search") {
            val filter = call.receive<CarSearchFilterRequest>()
            val cars = carRepository.search(filter)
            call.respond(HttpStatusCode.OK, cars.toGetCarsResponse())
        }

        // Update an existing car by ID
        put("/{id}") {
            call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
            val car = call.receive<UpdateCarRequest>()
            carRepository.update(car)
            call.respond(HttpStatusCode.Accepted)
        }

        post("/costofownership") {
            val calculation = call.receive<CostOfOwnerShipRequest>()
            val result = carRepository.calculateCostOfOwnerShip(calculation)
            call.respond(HttpStatusCode.OK, result)
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