package prof.db

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.entities.Car
import prof.entities.EntityAttribute
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum
import prof.enums.PowerSourceTypeEnum

object FakeCarRepository : CarRepository {

    private var currentId: Long = 0L
    private val cars = mutableListOf<Car>()
    private val entityAttributeRepo = FakeEntityAttributeRepository

    init {
        runBlocking {
            create(
                CreateCarRequest(
                    make = "Toyota",
                    model = "Corolla",
                    price = 20000.0f,
                    pickupLocation = "City Center",
                    category = "Sedan",
                    powerSourceType = PowerSourceTypeEnum.ICE,
                    color = "Grijs",
                    engineType = "1.8L I4",
                    enginePower = "90kW",
                    fuelType = "Benzine",
                    transmission = "Automatisch",
                    interiorType = "Stof",
                    interiorColor = "Zwart",
                    exteriorType = "Hatchback",
                    exteriorFinish = "Metallic",
                    wheelSize = "16 inch",
                    wheelType = "Lichtmetaal",
                    seats = 5,
                    doors = 4,
                    modelYear = 2020,
                    licensePlate = "AB-123-C",
                    mileage = 45000,
                    vinNumber = "JT1234567890XYZ01",
                    tradeName = "Corolla 1.8 Hybrid",
                    bpm = 3800f,
                    curbWeight = 1250,
                    maxWeight = 1800,
                    firstRegistrationDate = "2020-03-15",
                    imageFileNames = mutableListOf(),
                    createdAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )

            create(
                CreateCarRequest(
                    make = "Tesla",
                    model = "Model 3",
                    price = 45000.0f,
                    pickupLocation = "Airport",
                    category = "Sedan",
                    powerSourceType = PowerSourceTypeEnum.BEV,
                    color = "Wit",
                    engineType = "Dual Motor",
                    enginePower = "190kW",
                    fuelType = "Elektrisch",
                    transmission = "Automatisch",
                    interiorType = "Leder",
                    interiorColor = "Wit",
                    exteriorType = "Sedan",
                    exteriorFinish = "Glans",
                    wheelSize = "18 inch",
                    wheelType = "Alloy",
                    seats = 5,
                    doors = 4,
                    modelYear = 2023,
                    licensePlate = "EV-456-D",
                    mileage = 12000,
                    vinNumber = "5YJ3E1EA7LF123456",
                    tradeName = "Model 3 Long Range",
                    bpm = 0f,
                    curbWeight = 1750,
                    maxWeight = 2200,
                    firstRegistrationDate = "2023-02-01",
                    imageFileNames = mutableListOf(),
                    createdAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
        }
    }

    override suspend fun findById(id: Long): Car? {
        val car = cars.find { it.id == id } ?: return null
        car.attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id).toMutableList()
        return car
    }

    override suspend fun findAll(): List<Car> =
        cars.map { car ->
            car.apply {
                attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id).toMutableList()
            }
        }

    override suspend fun create(entity: CreateCarRequest): Car {
        currentId++
        val now = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Amsterdam"))

        val car = Car(
            id = currentId,
            imageFileNames = entity.imageFileNames.toMutableList(),
            createdAt = now,
            modifiedAt = now
        )
        cars.add(car)

        val attrs = mutableListOf<EntityAttribute>()

        // Automatisch alle niet-nulle velden toevoegen
        fun addAttr(enum: CarAttributeEnum, value: Any?) {
            if (value != null) attrs += EntityAttribute(
                id = 0,
                entity = EntityEnum.CAR,
                entityId = car.id,
                attribute = enum.name,
                value = value.toString(),
                createdAt = now,
                modifiedAt = now
            )
        }

        addAttr(CarAttributeEnum.MAKE, entity.make)
        addAttr(CarAttributeEnum.MODEL, entity.model)
        addAttr(CarAttributeEnum.PRICE, entity.price)
        addAttr(CarAttributeEnum.PICKUP_LOCATION, entity.pickupLocation)
        addAttr(CarAttributeEnum.CATEGORY, entity.category)
        addAttr(CarAttributeEnum.POWER_SOURCE_TYPE, entity.powerSourceType)
        addAttr(CarAttributeEnum.COLOR, entity.color)
        addAttr(CarAttributeEnum.ENGINE_TYPE, entity.engineType)
        addAttr(CarAttributeEnum.ENGINE_POWER, entity.enginePower)
        addAttr(CarAttributeEnum.FUEL_TYPE, entity.fuelType)
        addAttr(CarAttributeEnum.TRANSMISSION, entity.transmission)
        addAttr(CarAttributeEnum.INTERIOR_TYPE, entity.interiorType)
        addAttr(CarAttributeEnum.INTERIOR_COLOR, entity.interiorColor)
        addAttr(CarAttributeEnum.EXTERIOR_TYPE, entity.exteriorType)
        addAttr(CarAttributeEnum.EXTERIOR_FINISH, entity.exteriorFinish)
        addAttr(CarAttributeEnum.WHEEL_SIZE, entity.wheelSize)
        addAttr(CarAttributeEnum.WHEEL_TYPE, entity.wheelType)
        addAttr(CarAttributeEnum.SEATS, entity.seats)
        addAttr(CarAttributeEnum.DOORS, entity.doors)
        addAttr(CarAttributeEnum.MODEL_YEAR, entity.modelYear)
        addAttr(CarAttributeEnum.LICENSE_PLATE, entity.licensePlate)
        addAttr(CarAttributeEnum.MILEAGE, entity.mileage)
        addAttr(CarAttributeEnum.VIN_NUMBER, entity.vinNumber)
        addAttr(CarAttributeEnum.TRADE_NAME, entity.tradeName)
        addAttr(CarAttributeEnum.BPM, entity.bpm)
        addAttr(CarAttributeEnum.CURB_WEIGHT, entity.curbWeight)
        addAttr(CarAttributeEnum.MAX_WEIGHT, entity.maxWeight)
        addAttr(CarAttributeEnum.FIRST_REGISTRATION_DATE, entity.firstRegistrationDate)

        attrs.forEach { entityAttributeRepo.createBlocking(it) }

        car.attributes = attrs.toMutableList()
        return car
    }

    override suspend fun update(entity: UpdateCarRequest) {
        // analoog aan create(), zelfde veldafhandeling
    }

    override suspend fun addImageFileName(carId: Long, imageFileName: String) {
        val car = cars.find { it.id == carId } ?: return
        car.imageFileNames.add(imageFileName)
    }

    override suspend fun delete(id: Long): Boolean {
        val car = cars.find { it.id == id } ?: return false
        val attrs = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id)
        attrs.forEach { entityAttributeRepo.deleteBlocking(it.id) }
        cars.remove(car)
        return true
    }
}
