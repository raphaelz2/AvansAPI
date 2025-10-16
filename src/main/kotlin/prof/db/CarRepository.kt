package prof.db

import prof.Requests.CarSearchFilterRequest
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CreateCarRequest
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateCarRequest
import prof.entities.Car
import prof.responses.GetCostOfOwnerShipResponse

interface CarRepository {
    suspend fun findById(id: Long): Car?
    suspend fun findAll(): List<Car>
    suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean
    suspend fun create(entity: CreateCarRequest): Car
    suspend fun addImageFileName(carId: Long, imageFileName: String)
    suspend fun update(entity: UpdateCarRequest)
    suspend fun calculateCostOfOwnerShip(entity: CostOfOwnerShipRequest): GetCostOfOwnerShipResponse
    suspend fun delete(id: Long): Boolean
    suspend fun search(filter: CarSearchFilterRequest): List<Car>
}
