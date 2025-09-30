package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.entities.Car
import prof.entities.EntityAttribute
import prof.enums.CarAttributeEnum
import prof.enums.EntityEnum
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import prof.enums.PowerSourceTypeEnum

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

    private fun entityAttributesFromRequest(carId: Long, req: CreateCarRequest): List<EntityAttribute> {
        val now = LocalDateTime.parse(req.createdAt.toString())
        return listOfNotNull(
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.MAKE.name, req.make, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.MODEL.name, req.model, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.PRICE.name, req.price.toString(), now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.PICKUP_LOCATION.name, req.pickupLocation, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.CATEGORY.name, req.category, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.POWER_SOURCE_TYPE.name, req.powerSourceType.name, now, now)
        )
    }

    private fun entityAttributesFromRequest(carId: Long, req: UpdateCarRequest): List<EntityAttribute> {
        val now = LocalDateTime.parse(req.modifiedAt.toString())
        return listOfNotNull(
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.MAKE.name, req.make, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.MODEL.name, req.model, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.PRICE.name, req.price.toString(), now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.PICKUP_LOCATION.name, req.pickupLocation, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.CATEGORY.name, req.category, now, now),
            EntityAttribute(0, EntityEnum.CAR, carId, CarAttributeEnum.POWER_SOURCE_TYPE.name, req.powerSourceType.name, now, now)
        )
    }
}
