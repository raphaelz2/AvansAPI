package prof.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.AuthenticatedUser
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepositoryInterface
import prof.enums.PowerSourceTypeEnum
import prof.mapperExtentions.toGetCarResponse
import prof.mapperExtentions.toGetCarsResponse
import java.io.File
import java.util.UUID

fun Route.carRoutes(carRepository: CarRepositoryInterface) {
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

        authenticate("auth-jwt") {
            post("/create") {
                val principal = call.principal<AuthenticatedUser>()
                val userId = principal?.id
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, "User not authenticated")

                val carRequest = call.receive<CreateCarRequest>()
                val carWithUser = carRequest.copy(userId = userId)

                val createdCar = carRepository.create(carWithUser)
                call.respond(HttpStatusCode.Created, createdCar.toGetCarResponse())
            }
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

        post("/car-image/{carId}") {
            val carId = call.parameters["carId"]?.toLongOrNull()

            if (carId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid car ID")
                return@post
            }

            val existingCar = carRepository.findById(carId)
            if (existingCar == null) {
                call.respond(HttpStatusCode.NotFound, "Car not found")
                return@post
            }

            val multipart = call.receiveMultipart()
            val uploadedFileNames = mutableListOf<String>()

            val uploadDir = File("uploads/cars")
            if (!uploadDir.exists()) {
                uploadDir.mkdirs()
            }

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: UUID.randomUUID().toString()
                        val fileExtension = fileName.substringAfterLast(".", "jpg")
                        val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"

                        val file = File(uploadDir, uniqueFileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }

                        uploadedFileNames.add(uniqueFileName)
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (uploadedFileNames.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "No images uploaded")
                return@post
            }

            carRepository.addImages(carId, uploadedFileNames)

            val updatedCar = carRepository.findById(carId)
            call.respond(HttpStatusCode.OK, updatedCar!!.toGetCarResponse())
        }

    }
}
private fun getImageUploadPath(imageFile: String) = "uploads/$imageFile"