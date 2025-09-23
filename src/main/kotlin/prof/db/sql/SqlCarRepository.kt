package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.db.CarRepository
import prof.entities.Car
import prof.enums.PowerSourceTypeEnum

class SqlCarRepository : CarRepository {
    private fun rowToCar(row: ResultRow): Car {
        val id = row[Cars.id]
        val images = transaction {
            CarImages
                .selectAll()
                .where { CarImages.carId eq id }
                .map { it[CarImages.filename] }
                .toMutableList()
        }
        return Car(
            id = id,
            make = row[Cars.make],
            model = row[Cars.model],
            price = row[Cars.price],
            pickupLocation = row[Cars.pickupLocation],
            category = row[Cars.category],
            powerSourceType = PowerSourceTypeEnum.valueOf(row[Cars.powerSourceType]),
            imageFileNames = images,
            createdAt = LocalDateTime.parse(row[Cars.createdAt]),
            modifiedAt = LocalDateTime.parse(row[Cars.modifiedAt])
        )
    }

    override suspend fun findById(id: Long): Car? = transaction {
        Cars
            .selectAll()
            .where { Cars.id eq id }
            .singleOrNull()
            ?.let { rowToCar(it) }
    }

    override suspend fun findAll(): List<Car> = transaction {
        Cars
            .selectAll()
            .map { rowToCar(it) }
    }

    override suspend fun create(entity: CreateCarRequest): Car = transaction {
        val newId: Long = Cars.insert { st ->
            st[make] = entity.make
            st[model] = entity.model
            st[price] = entity.price
            st[pickupLocation] = entity.pickupLocation
            st[category] = entity.category
            st[powerSourceType] = entity.powerSourceType.name
            st[createdAt] = entity.createdAt.toString()
            st[modifiedAt] = entity.modifiedAt.toString()
        } get Cars.id

        // insert images
        entity.imageFileNames.forEach { fn ->
            CarImages.insert { st ->
                st[carId] = newId
                st[filename] = fn
            }
        }

        // fetch created row and map, without calling a suspend fun
        Cars
            .selectAll()
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
            st[make] = entity.make
            st[model] = entity.model
            st[price] = entity.price
            st[pickupLocation] = entity.pickupLocation
            st[category] = entity.category
            st[powerSourceType] = entity.powerSourceType.name
            st[createdAt] = entity.createdAt.toString()
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
    }

    override suspend fun delete(id: Long): Boolean = transaction {
        CarImages.deleteWhere { CarImages.carId eq id }
        Cars.deleteWhere { Cars.id eq id } > 0
    }
}
