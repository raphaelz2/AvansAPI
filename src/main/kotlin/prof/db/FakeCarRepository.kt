package prof.db

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CreateCarRequest
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateCarRequest
import prof.db.sql.Cars
import prof.entities.Car
import prof.entities.EntityAttribute
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum
import prof.enums.PowerSourceTypeEnum
import prof.utils.LocationUtils
import kotlin.text.isNullOrBlank
import prof.responses.GetCostOfOwnerShipResponse
import java.math.BigDecimal
import java.math.RoundingMode

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
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    bookingCost = "25.00",
                    costPerKilometer = 0.29,
                    deposit = "100",
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
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    bookingCost = "25.00",
                    costPerKilometer = 0.29,
                    deposit = "100",
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

    override suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean {
        return true
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
        addAttr(CarAttributeEnum.BOOKING_COST, entity.bookingCost)
        addAttr(CarAttributeEnum.DEPOSIT, entity.deposit)
        addAttr(CarAttributeEnum.COST_PER_KILOMETER, entity.costPerKilometer)

        attrs.forEach { entityAttributeRepo.createBlocking(it) }

        car.attributes = attrs.toMutableList()
        return car
    }

    override suspend fun update(entity: UpdateCarRequest) {
        // analoog aan create(), zelfde veldafhandeling
    }

    override suspend fun search(filter: CarSearchFilterRequest): List<Car> = transaction {
        val allCars = cars.map { car ->
            car.apply {
                attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id).toMutableList()
            }
        }

        var filteredCars = allCars

        if (filter.latitude != null && filter.longitude != null && filter.maxDistanceKm != null) {
            filteredCars = filteredCars.filter { car ->
                val locationAttr = car.attributes.find {
                    it.attribute == CarAttributeEnum.PICKUP_LOCATION.name
                }

                if (locationAttr == null) return@filter false

                val coords = LocationUtils.parseCoordinates(locationAttr.value)
                if (coords == null) return@filter false

                val distance = LocationUtils.calculateDistance(
                    filter.latitude!!,
                    filter.longitude!!,
                    coords.first,
                    coords.second
                )

                distance <= filter.maxDistanceKm!!
            }
        }

        if (!filter.make.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.MAKE.name &&
                            it.value.equals(filter.make, ignoreCase = true)
                }
            }
        }

        if (!filter.model.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.MODEL.name &&
                            it.value.equals(filter.model, ignoreCase = true)
                }
            }
        }

        if (filter.powerSourceType != null) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.POWER_SOURCE_TYPE.name &&
                            it.value.equals(filter.powerSourceType.name, ignoreCase = true)
                }
            }
        }

        if (!filter.category.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.CATEGORY.name &&
                            it.value.equals(filter.category, ignoreCase = true)
                }
            }
        }

        if (!filter.fuelType.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.FUEL_TYPE.name &&
                            it.value.equals(filter.fuelType, ignoreCase = true)
                }
            }
        }

        if (!filter.transmission.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.TRANSMISSION.name &&
                            it.value.equals(filter.transmission, ignoreCase = true)
                }
            }
        }

        if (!filter.color.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.COLOR.name &&
                            it.value.equals(filter.color, ignoreCase = true)
                }
            }
        }

        if (!filter.interiorColor.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.INTERIOR_COLOR.name &&
                            it.value.equals(filter.interiorColor, ignoreCase = true)
                }
            }
        }

        if (!filter.exteriorType.isNullOrBlank()) {
            filteredCars = filteredCars.filter { car ->
                car.attributes.any {
                    it.attribute == CarAttributeEnum.EXTERIOR_TYPE.name &&
                            it.value.equals(filter.exteriorType, ignoreCase = true)
                }
            }
        }

        if (filter.minPrice != null || filter.maxPrice != null) {
            filteredCars = filteredCars.filter { car ->
                val priceAttr = car.attributes.find { it.attribute == CarAttributeEnum.PRICE.name }
                val price = priceAttr?.value?.toDoubleOrNull()

                if (price == null) return@filter false

                val meetsMin = filter.minPrice == null || price >= filter.minPrice
                val meetsMax = filter.maxPrice == null || price <= filter.maxPrice

                meetsMin && meetsMax
            }
        }

        if (filter.minSeats != null || filter.maxSeats != null) {
            filteredCars = filteredCars.filter { car ->
                val seatsAttr = car.attributes.find { it.attribute == CarAttributeEnum.SEATS.name }
                val seats = seatsAttr?.value?.toIntOrNull()

                if (seats == null) return@filter false

                val meetsMin = filter.minSeats == null || seats >= filter.minSeats
                val meetsMax = filter.maxSeats == null || seats <= filter.maxSeats

                meetsMin && meetsMax
            }
        }

        if (filter.minDoors != null || filter.maxDoors != null) {
            filteredCars = filteredCars.filter { car ->
                val doorsAttr = car.attributes.find { it.attribute == CarAttributeEnum.DOORS.name }
                val doors = doorsAttr?.value?.toIntOrNull()

                if (doors == null) return@filter false

                val meetsMin = filter.minDoors == null || doors >= filter.minDoors
                val meetsMax = filter.maxDoors == null || doors <= filter.maxDoors

                meetsMin && meetsMax
            }
        }

        if (filter.minModelYear != null || filter.maxModelYear != null) {
            filteredCars = filteredCars.filter { car ->
                val yearAttr = car.attributes.find { it.attribute == CarAttributeEnum.MODEL_YEAR.name }
                val year = yearAttr?.value?.toIntOrNull()

                if (year == null) return@filter false

                val meetsMin = filter.minModelYear == null || year >= filter.minModelYear
                val meetsMax = filter.maxModelYear == null || year <= filter.maxModelYear

                meetsMin && meetsMax
            }
        }

        if (filter.minMileage != null || filter.maxMileage != null) {
            filteredCars = filteredCars.filter { car ->
                val mileageAttr = car.attributes.find { it.attribute == CarAttributeEnum.MILEAGE.name }
                val mileage = mileageAttr?.value?.toIntOrNull()

                if (mileage == null) return@filter false

                val meetsMin = filter.minMileage == null || mileage >= filter.minMileage
                val meetsMax = filter.maxMileage == null || mileage <= filter.maxMileage

                meetsMin && meetsMax
            }
        }

        if (!filter.searchQuery.isNullOrBlank()) {
            val query = filter.searchQuery.lowercase()
            filteredCars = filteredCars.filter { car ->
                car.attributes.any { attr ->
                    when (attr.attribute) {
                        CarAttributeEnum.MAKE.name,
                        CarAttributeEnum.MODEL.name,
                        CarAttributeEnum.COLOR.name,
                        CarAttributeEnum.TRADE_NAME.name,
                        CarAttributeEnum.LICENSE_PLATE.name -> {
                            attr.value.lowercase().contains(query)
                        }
                        else -> false
                    }
                }
            }
        }

        filteredCars
    }

    override suspend fun calculateCostOfOwnerShip(entity: CostOfOwnerShipRequest): GetCostOfOwnerShipResponse {
        val car = findById(entity.carId)
            ?: throw IllegalArgumentException("Car not found")

        val category = car.getAttribute(CarAttributeEnum.CATEGORY) ?: "Standard"
        val powerSource = car.getAttributeEnum(CarAttributeEnum.POWER_SOURCE_TYPE, PowerSourceTypeEnum::class.java)
            ?: PowerSourceTypeEnum.ICE
        val price = BigDecimal(car.getAttributeFloat(CarAttributeEnum.PRICE)?.toString() ?: "0.0")

        val avgConsumption = when (powerSource) {
            PowerSourceTypeEnum.ICE -> BigDecimal("7.5")
            PowerSourceTypeEnum.HEV -> BigDecimal("5.0")
            PowerSourceTypeEnum.BEV -> BigDecimal("18.0")
            PowerSourceTypeEnum.FCEV -> BigDecimal("1.0")
        }

        val energyPrice = when (powerSource) {
            PowerSourceTypeEnum.BEV -> BigDecimal("0.25")
            PowerSourceTypeEnum.FCEV -> BigDecimal("12.0")
            else -> BigDecimal(entity.energyPricePerUnit.toString())
        }

        val yearlyEnergyCost = BigDecimal(entity.kilometersPerYear)
            .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
            .multiply(avgConsumption)
            .multiply(energyPrice)
            .setScale(2, RoundingMode.HALF_UP)

        val yearlyMaintenance = when (category.lowercase()) {
            "luxury" -> BigDecimal("1200.00")
            "suv" -> BigDecimal("900.00")
            "compact" -> BigDecimal("600.00")
            else -> BigDecimal("750.00")
        }

        val yearlyDepreciation = price
            .multiply(BigDecimal("0.15"))
            .setScale(2, RoundingMode.HALF_UP)

        val total = yearlyEnergyCost
            .add(yearlyMaintenance)
            .add(yearlyDepreciation)
            .setScale(2, RoundingMode.HALF_UP)

        return GetCostOfOwnerShipResponse(
            carId = entity.carId,
            category = category,
            powerSourceType = powerSource.name,
            kilometersPerYear = entity.kilometersPerYear,
            energyPricePerUnit = energyPrice.toDouble(),
            averageConsumptionPer100Km = avgConsumption.toDouble(),
            yearlyEnergyCost = yearlyEnergyCost.toDouble(),
            yearlyDepreciation = yearlyDepreciation.toDouble(),
            yearlyMaintenanceCost = yearlyMaintenance.toDouble(),
            totalYearlyCost = total.toDouble()
        )
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
