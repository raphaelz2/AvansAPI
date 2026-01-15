package prof.db.sql

import CarRequestWithUser
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CreateCarRequest
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepositoryInterface
import prof.db.sql.migrations.CarImages
import prof.db.sql.migrations.Cars
import prof.db.sql.migrations.Reservations
import prof.entities.CarDTO
import prof.entities.EntityAttributeDTO
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum
import prof.utils.LocationUtils
import prof.enums.PowerSourceTypeEnum
import prof.enums.ReservationStatusEnum
import prof.responses.GetCostOfOwnerShipResponse
import java.math.BigDecimal
import java.math.RoundingMode

class SqlCarRepository(
    private val entityAttributeRepo: SqlEntityAttributeRepository
) : CarRepositoryInterface {

    private fun rowToCar(row: ResultRow): CarDTO {
        val id = row[Cars.id]

        val images = CarImages.selectAll()
            .where { CarImages.carId eq id }
            .map { it[CarImages.filename] }
            .toMutableList()

        val attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, id).toMutableList()

        return CarDTO(
            id = id,
            userId = row[Cars.userId],
            imageFileNames = images,
            createdAt = row[Cars.createdAt],
            modifiedAt = row[Cars.modifiedAt],
            attributes = attributes
        )
    }

    override suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean = transaction {
        val start = entity.startTime.toString()
        val end = entity.endTime.toString()

        val overlappingReservations = Reservations.selectAll()
            .where {
                (Reservations.startTime lessEq end) and
                        (Reservations.endTime greaterEq start) and
                        (Reservations.carId eq entity.carId) and
                        (Reservations.status eq ReservationStatusEnum.CONFIRMED.value)
            }
            .count()

        overlappingReservations == 0L
    }

    override suspend fun search(filter: CarSearchFilterRequest): List<CarDTO> = transaction {
        val allCars = Cars.selectAll().map { rowToCar(it) }

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

    override suspend fun findById(id: Long): CarDTO? = transaction {
        Cars.selectAll()
            .where { Cars.id eq id }
            .singleOrNull()
            ?.let { rowToCar(it) }
    }

    override suspend fun findAll(): List<CarDTO> = transaction {
        Cars.selectAll()
            .map { rowToCar(it) }
    }

    override suspend fun create(entity: CarRequestWithUser): CarDTO = transaction {
        val newId: Long = Cars.insert { st ->
            st[userId] = entity.userId
            st[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
            st[modifiedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        } get Cars.id

        val createCarRequest = CreateCarRequest(
            make = entity.make,
            model = entity.model,
            price = entity.price,
            pickupLocation = entity.pickupLocation,
            category = entity.category,
            powerSourceType = entity.powerSourceType,
            color = entity.color,
            engineType = entity.engineType,
            enginePower = entity.enginePower,
            fuelType = entity.fuelType,
            transmission = entity.transmission,
            interiorType = entity.interiorType,
            interiorColor = entity.interiorColor,
            exteriorType = entity.exteriorType,
            exteriorFinish = entity.exteriorFinish,
            wheelSize = entity.wheelSize,
            wheelType = entity.wheelType,
            seats = entity.seats,
            doors = entity.doors,
            modelYear = entity.modelYear,
            licensePlate = entity.licensePlate,
            mileage = entity.mileage,
            vinNumber = entity.vinNumber,
            tradeName = entity.tradeName,
            bpm = entity.bpm,
            curbWeight = entity.curbWeight,
            maxWeight = entity.maxWeight,
            firstRegistrationDate = entity.firstRegistrationDate,
            bookingCost = entity.bookingCost,
            costPerKilometer = entity.costPerKilometer,
            deposit = entity.deposit,
        )

        entityAttributesFromRequest(newId, createCarRequest).forEach { attr ->
            entityAttributeRepo.createBlocking(attr)
        }

        Cars.selectAll()
            .where { Cars.id eq newId }
            .single()
            .let { rowToCar(it) }
    }

    override suspend fun addImageFileName(carId: Long, imageFileName: String) {
        transaction {
            CarImages.insert { st ->
                st[CarImages.carId] = carId
                st[filename] = imageFileName
            }
        }
    }

    override suspend fun update(entity: UpdateCarRequest) = transaction {
        Cars.update({ Cars.id eq entity.id }) { st ->
            st[modifiedAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        }

        if (entity.imageFileNames.isNotEmpty()) {
            CarImages.deleteWhere { CarImages.carId eq entity.id }
            entity.imageFileNames.forEach { fn ->
                CarImages.insert { st ->
                    st[carId] = entity.id
                    st[filename] = fn
                }
            }
        }

        entityAttributesFromRequest(entity.id, entity).forEach { attr ->
            val existing = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, entity.id)
                .find { it.attribute == attr.attribute }

            if (existing != null) {
                attr.id = existing.id
                entityAttributeRepo.updateBlocking(attr)
            } else {
                entityAttributeRepo.createBlocking(attr)
            }
        }
    }

    override suspend fun calculateCostOfOwnerShip(entity: CostOfOwnerShipRequest): GetCostOfOwnerShipResponse {
        val car = findById(entity.carId)
            ?: throw IllegalArgumentException("Car not found")

        val category = car.getAttribute(CarAttributeEnum.CATEGORY) ?: "Standard"
        val powerSource = car.getAttributeEnum(CarAttributeEnum.POWER_SOURCE_TYPE, PowerSourceTypeEnum::class.java)
            ?: PowerSourceTypeEnum.ICE
        val price = car.getAttributeFloat(CarAttributeEnum.PRICE)?.toBigDecimal() ?: BigDecimal.ZERO

        val avgConsumption = when (powerSource) {
            PowerSourceTypeEnum.ICE -> 7.5
            PowerSourceTypeEnum.HEV -> 5.0
            PowerSourceTypeEnum.BEV -> 18.0
            PowerSourceTypeEnum.FCEV -> 1.0
        }

        val energyPrice = if (entity.energyPricePerUnit > 0)
            entity.energyPricePerUnit
        else when (powerSource) {
            PowerSourceTypeEnum.BEV -> 0.25
            PowerSourceTypeEnum.FCEV -> 12.0
            else -> 2.0
        }


        val yearlyEnergyCost = BigDecimal(entity.kilometersPerYear)
            .divide(BigDecimal(100), 10, RoundingMode.HALF_UP)
            .multiply(BigDecimal(avgConsumption))
            .multiply(BigDecimal(energyPrice))
            .setScale(2, RoundingMode.HALF_UP)

        val yearlyMaintenance = when (category.lowercase()) {
            "luxury" -> BigDecimal(1200)
            "suv" -> BigDecimal(900)
            "compact" -> BigDecimal(600)
            else -> BigDecimal(750)
        }

        val yearlyDepreciation = price.multiply(BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP)
        val total = yearlyEnergyCost.add(yearlyMaintenance).add(yearlyDepreciation)

        return GetCostOfOwnerShipResponse(
            carId = entity.carId,
            category = category,
            powerSourceType = powerSource.name,
            kilometersPerYear = entity.kilometersPerYear,
            energyPricePerUnit = energyPrice,
            averageConsumptionPer100Km = avgConsumption,
            yearlyEnergyCost = yearlyEnergyCost.toDouble(),
            yearlyDepreciation = yearlyDepreciation.toDouble(),
            yearlyMaintenanceCost = yearlyMaintenance.toDouble(),
            totalYearlyCost = total.toDouble()
        )
    }

    override suspend fun delete(id: Long): Boolean = transaction {
        CarImages.deleteWhere { CarImages.carId eq id }

        val attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, id)
        attributes.forEach { entityAttributeRepo.deleteBlocking(it.id) }

        Cars.deleteWhere { Cars.id eq id } > 0
    }

    private fun entityAttributesFromRequest(carId: Long, req: CreateCarRequest): List<EntityAttributeDTO> {
        val now = Clock.System.now().toString()
        val attrs = mutableListOf<EntityAttributeDTO>()

        fun add(enum: CarAttributeEnum, value: Any?) {
            if (value != null) attrs += EntityAttributeDTO(
                0,
                EntityEnum.CAR,
                carId,
                enum.name,
                value.toString(),
                now,
                now
            )
        }

        add(CarAttributeEnum.MAKE, req.make)
        add(CarAttributeEnum.MODEL, req.model)
        add(CarAttributeEnum.PRICE, req.price)
        add(CarAttributeEnum.PICKUP_LOCATION, req.pickupLocation)
        add(CarAttributeEnum.CATEGORY, req.category)
        add(CarAttributeEnum.POWER_SOURCE_TYPE, req.powerSourceType.name)
        add(CarAttributeEnum.COLOR, req.color)
        add(CarAttributeEnum.ENGINE_TYPE, req.engineType)
        add(CarAttributeEnum.ENGINE_POWER, req.enginePower)
        add(CarAttributeEnum.FUEL_TYPE, req.fuelType)
        add(CarAttributeEnum.TRANSMISSION, req.transmission)
        add(CarAttributeEnum.INTERIOR_TYPE, req.interiorType)
        add(CarAttributeEnum.INTERIOR_COLOR, req.interiorColor)
        add(CarAttributeEnum.EXTERIOR_TYPE, req.exteriorType)
        add(CarAttributeEnum.EXTERIOR_FINISH, req.exteriorFinish)
        add(CarAttributeEnum.WHEEL_SIZE, req.wheelSize)
        add(CarAttributeEnum.WHEEL_TYPE, req.wheelType)
        add(CarAttributeEnum.SEATS, req.seats)
        add(CarAttributeEnum.DOORS, req.doors)
        add(CarAttributeEnum.MODEL_YEAR, req.modelYear)
        add(CarAttributeEnum.LICENSE_PLATE, req.licensePlate)
        add(CarAttributeEnum.MILEAGE, req.mileage)
        add(CarAttributeEnum.VIN_NUMBER, req.vinNumber)
        add(CarAttributeEnum.TRADE_NAME, req.tradeName)
        add(CarAttributeEnum.BPM, req.bpm)
        add(CarAttributeEnum.CURB_WEIGHT, req.curbWeight)
        add(CarAttributeEnum.MAX_WEIGHT, req.maxWeight)
        add(CarAttributeEnum.FIRST_REGISTRATION_DATE, req.firstRegistrationDate)
        add(CarAttributeEnum.COST_PER_KILOMETER, req.costPerKilometer)
        add(CarAttributeEnum.DEPOSIT, req.deposit)
        add(CarAttributeEnum.BOOKING_COST, req.bookingCost)

        return attrs
    }

    private fun entityAttributesFromRequest(carId: Long, req: UpdateCarRequest): List<EntityAttributeDTO> {
        val now = Clock.System.now().toString()
        val attrs = mutableListOf<EntityAttributeDTO>()

        fun add(enum: CarAttributeEnum, value: Any?) {
            if (value != null) attrs += EntityAttributeDTO(
                0,
                EntityEnum.CAR,
                carId,
                enum.name,
                value.toString(),
                now,
                now
            )
        }

        add(CarAttributeEnum.MAKE, req.make)
        add(CarAttributeEnum.MODEL, req.model)
        add(CarAttributeEnum.PRICE, req.price)
        add(CarAttributeEnum.PICKUP_LOCATION, req.pickupLocation)
        add(CarAttributeEnum.CATEGORY, req.category)
        add(CarAttributeEnum.POWER_SOURCE_TYPE, req.powerSourceType.name)
        add(CarAttributeEnum.COLOR, req.color)
        add(CarAttributeEnum.ENGINE_TYPE, req.engineType)
        add(CarAttributeEnum.ENGINE_POWER, req.enginePower)
        add(CarAttributeEnum.FUEL_TYPE, req.fuelType)
        add(CarAttributeEnum.TRANSMISSION, req.transmission)
        add(CarAttributeEnum.INTERIOR_TYPE, req.interiorType)
        add(CarAttributeEnum.INTERIOR_COLOR, req.interiorColor)
        add(CarAttributeEnum.EXTERIOR_TYPE, req.exteriorType)
        add(CarAttributeEnum.EXTERIOR_FINISH, req.exteriorFinish)
        add(CarAttributeEnum.WHEEL_SIZE, req.wheelSize)
        add(CarAttributeEnum.WHEEL_TYPE, req.wheelType)
        add(CarAttributeEnum.SEATS, req.seats)
        add(CarAttributeEnum.DOORS, req.doors)
        add(CarAttributeEnum.MODEL_YEAR, req.modelYear)
        add(CarAttributeEnum.LICENSE_PLATE, req.licensePlate)
        add(CarAttributeEnum.MILEAGE, req.mileage)
        add(CarAttributeEnum.VIN_NUMBER, req.vinNumber)
        add(CarAttributeEnum.TRADE_NAME, req.tradeName)
        add(CarAttributeEnum.BPM, req.bpm)
        add(CarAttributeEnum.CURB_WEIGHT, req.curbWeight)
        add(CarAttributeEnum.MAX_WEIGHT, req.maxWeight)
        add(CarAttributeEnum.FIRST_REGISTRATION_DATE, req.firstRegistrationDate)
        add(CarAttributeEnum.COST_PER_KILOMETER, req.costPerKilometer)
        add(CarAttributeEnum.DEPOSIT, req.deposit)
        add(CarAttributeEnum.BOOKING_COST, req.bookingCost)

        return attrs
    }

    override suspend fun addImages(carId: Long, fileNames: List<String>): Boolean = transaction {
        val carExists = Cars.selectAll()
            .where { Cars.id eq carId }
            .count() > 0

        if (!carExists) {
            return@transaction false
        }

        fileNames.forEach { fileName ->
            CarImages.insert { st ->
                st[CarImages.carId] = carId
                st[filename] = fileName
            }
        }

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        Cars.update({ Cars.id eq carId }) {
            it[modifiedAt] = now
        }

        true
    }

    override suspend fun findByUserId(userId: Long): List<CarDTO> = transaction {
        Cars.selectAll()
            .where { Cars.userId eq userId }
            .map { rowToCar(it) }
    }
}
