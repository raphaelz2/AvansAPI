package prof.db

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.entities.Car
import prof.enums.PowerSourceType

object FakeCarRepository : CarRepository {
    private var currentId: Long = 0L
    private val cars = mutableListOf<Car>()

    // Seed the fake CarRepository with some dummy data
    init {
        runBlocking {
            create(
                CreateCarRequest(
                    make = "Toyota",
                    model = "Corolla",
                    price = 20.0f,
                    pickupLocation = "City Center",
                    category = "Sedan",
                    powerSourceType = PowerSourceType.ICE,
                    imageFileNames = mutableListOf(),
                    createdAt =  LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt =  LocalDateTime(2024, 3, 27, 2, 16, 20)                )
            )
            create(
                CreateCarRequest(
                    make = "Tesla",
                    model = "Model 3",
                    price = 35.0f,
                    pickupLocation = "Airport",
                    category = "Sedan",
                    powerSourceType = PowerSourceType.BEV,
                    imageFileNames = mutableListOf(),
                    createdAt =  LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt =  LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateCarRequest(
                    make = "Honda",
                    model = "Clarity",
                    price = 30.0f,
                    pickupLocation = "Downtown",
                    category = "Sedan",
                    powerSourceType = PowerSourceType.HEV,
                    createdAt =  LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt =  LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateCarRequest(
                    make = "Hyundai",
                    model = "Nexo",
                    price = 50.0f,
                    pickupLocation = "Station",
                    category = "SUV",
                    powerSourceType = PowerSourceType.FCEV,
                    imageFileNames = mutableListOf(),
                    createdAt =  LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt =  LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
        }
    }

    // Find a car by its ID
    override suspend fun findById(id: Long): Car? = cars.find { it.id == id }

    // Find all cars
    override suspend fun findAll(): List<Car> = cars.toList()

    // Create a new car
    override suspend fun create(entity: CreateCarRequest): Car {
        currentId++ // Increment the ID for the new car
        val now = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Amsterdam")) // Get current time in Amsterdam
        val newCar = Car(
            id = currentId,
            make = entity.make,
            model = entity.model,
            price = entity.price,
            pickupLocation = entity.pickupLocation,
            category = entity.category,
            powerSourceType = entity.powerSourceType,
            imageFileNames = entity.imageFileNames.toMutableList(), // Copy the list if necessary
            createdAt = now,
            modifiedAt = now
        )
        cars.add(newCar) // Add the new car to the list
        return newCar // Return the newly created car
    }

    // Update an existing car
    override suspend fun update(entity: UpdateCarRequest) {
        val car = cars.find { it.id == entity.id } ?: throw IllegalArgumentException("Car with ID ${entity.id} does not exist")

        // Update the properties of the found car to match those in the update request
        car.apply {
            make = entity.make
            model = entity.model
            price = entity.price
            pickupLocation = entity.pickupLocation
            category = entity.category
            powerSourceType = entity.powerSourceType
            imageFileNames = entity.imageFileNames.toMutableList() // Make a copy of the list if necessary
            createdAt = entity.createdAt
            modifiedAt = entity.modifiedAt
        }
    }

    override suspend fun addImageFileName(carId: Long, imageFileName: String) {
        val car = cars.find { it.id == carId }
            ?: throw IllegalArgumentException("Car with ID $carId not found")

        // Add the image file name to the car
        car.imageFileNames.add(imageFileName)
    }

    // Delete a car by its ID
    override suspend fun delete(id: Long): Boolean = cars.removeIf { it.id == id }
}
