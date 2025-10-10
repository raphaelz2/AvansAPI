package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.entities.Car
import prof.entities.EntityAttribute
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum
import prof.utils.LocationUtils

class SqlCarRepository(
    private val entityAttributeRepo: SqlEntityAttributeRepository
) : CarRepository {

    private fun rowToCar(row: ResultRow): Car {
        val id = row[Cars.id]

        val images = CarImages.selectAll()
            .where { CarImages.carId eq id }
            .map { it[CarImages.filename] }
            .toMutableList()

        val attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, id).toMutableList()

        return Car(
            id = id,
            imageFileNames = images,
            createdAt = LocalDateTime.parse(row[Cars.createdAt]),
            modifiedAt = LocalDateTime.parse(row[Cars.modifiedAt]),
            attributes = attributes
        )
    }

    override suspend fun search(filter: CarSearchFilterRequest): List<Car> = transaction {
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

    override suspend fun findById(id: Long): Car? = transaction {
        Cars.selectAll()
            .where { Cars.id eq id }
            .singleOrNull()
            ?.let { rowToCar(it) }
    }

    override suspend fun findAll(): List<Car> = transaction {
        Cars.selectAll()
            .map { rowToCar(it) }
    }

    override suspend fun create(entity: CreateCarRequest): Car = transaction {
        val newId: Long = Cars.insert { st ->
            st[createdAt] = entity.createdAt.toString()
            st[modifiedAt] = entity.modifiedAt.toString()
        } get Cars.id

        entity.imageFileNames.forEach { fn ->
            CarImages.insert { st ->
                st[carId] = newId
                st[filename] = fn
            }
        }

        entityAttributesFromRequest(newId, entity).forEach { attr ->
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
            st[modifiedAt] = entity.modifiedAt.toString()
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

    override suspend fun delete(id: Long): Boolean = transaction {
        CarImages.deleteWhere { CarImages.carId eq id }

        val attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, id)
        attributes.forEach { entityAttributeRepo.deleteBlocking(it.id) }

        Cars.deleteWhere { Cars.id eq id } > 0
    }

    // --- Nieuwe dynamische mapping voor CreateCarRequest ---
    private fun entityAttributesFromRequest(carId: Long, req: CreateCarRequest): List<EntityAttribute> {
        val now = req.createdAt

        val attrs = mutableListOf<EntityAttribute>()

        fun add(enum: CarAttributeEnum, value: Any?) {
            if (value != null) attrs += EntityAttribute(
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

        return attrs
    }

    // --- Nieuwe dynamische mapping voor UpdateCarRequest ---
    private fun entityAttributesFromRequest(carId: Long, req: UpdateCarRequest): List<EntityAttribute> {
        val now = req.modifiedAt
        val attrs = mutableListOf<EntityAttribute>()

        fun add(enum: CarAttributeEnum, value: Any?) {
            if (value != null) attrs += EntityAttribute(
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

        return attrs
    }
}
