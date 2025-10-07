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
                    price = 20.0f,
                    pickupLocation = "City Center",
                    category = "Sedan",
                    powerSourceType = PowerSourceTypeEnum.ICE,
                    imageFileNames = mutableListOf(),
                    createdAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateCarRequest(
                    make = "Tesla",
                    model = "Model 3",
                    price = 35.0f,
                    pickupLocation = "Airport",
                    category = "Sedan",
                    powerSourceType = PowerSourceTypeEnum.BEV,
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

        val attrs = listOf(
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.MAKE.name, entity.make, now, now),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.MODEL.name, entity.model, now, now),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.PRICE.name, entity.price.toString(), now, now),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.PICKUP_LOCATION.name, entity.pickupLocation, now, now),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.CATEGORY.name, entity.category, now, now),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.POWER_SOURCE_TYPE.name, entity.powerSourceType.name, now, now)
        )
        attrs.forEach { entityAttributeRepo.createBlocking(it) }

        car.attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id).toMutableList()
        return car
    }

    override suspend fun update(entity: UpdateCarRequest) {
        val car = cars.find { it.id == entity.id } ?: return
        car.imageFileNames = entity.imageFileNames.toMutableList()
        car.modifiedAt = entity.modifiedAt

        val attrs = listOf(
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.MAKE.name, entity.make, car.createdAt, car.modifiedAt),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.MODEL.name, entity.model, car.createdAt, car.modifiedAt),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.PRICE.name, entity.price.toString(), car.createdAt, car.modifiedAt),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.PICKUP_LOCATION.name, entity.pickupLocation, car.createdAt, car.modifiedAt),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.CATEGORY.name, entity.category, car.createdAt, car.modifiedAt),
            EntityAttribute(0, EntityEnum.CAR, car.id, CarAttributeEnum.POWER_SOURCE_TYPE.name, entity.powerSourceType.name, car.createdAt, car.modifiedAt)
        )

        attrs.forEach { attr ->
            val existing = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id)
                .find { it.attribute == attr.attribute }

            if (existing != null) {
                attr.id = existing.id
                entityAttributeRepo.updateBlocking(attr)
            } else {
                entityAttributeRepo.createBlocking(attr)
            }
        }

        car.attributes = entityAttributeRepo.findByEntityBlocking(EntityEnum.CAR.name, car.id).toMutableList()
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
