package prof.db

import prof.Requests.CarSearchFilterRequest
import prof.Requests.CreateCarRequest
import prof.Requests.UpdateCarRequest
import prof.entities.Car

interface CarRepository {
    suspend fun findById(id: Long): Car?
    suspend fun findAll(): List<Car>
    suspend fun create(entity: CreateCarRequest): Car
    suspend fun addImageFileName(carId: Long, imageFileName: String)
    suspend fun update(entity: UpdateCarRequest)
    suspend fun delete(id: Long): Boolean
    suspend fun search(filter: CarSearchFilterRequest): List<Car>
}
