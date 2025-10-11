package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.entities.Car
import prof.entities.EntityAttribute
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum

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
