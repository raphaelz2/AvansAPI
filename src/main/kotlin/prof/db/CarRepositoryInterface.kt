package prof.db

import CarRequestWithUser
import prof.Requests.CarSearchFilterRequest
import prof.Requests.CostOfOwnerShipRequest
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateCarRequest
import prof.entities.CarDTO
import prof.responses.GetCostOfOwnerShipResponse

interface CarRepositoryInterface {
    suspend fun findById(id: Long): CarDTO?
    suspend fun findAll(): List<CarDTO>
    suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean
    suspend fun create(entity: CarRequestWithUser): CarDTO
    suspend fun addImageFileName(carId: Long, imageFileName: String)
    suspend fun update(entity: UpdateCarRequest)
    suspend fun calculateCostOfOwnerShip(entity: CostOfOwnerShipRequest): GetCostOfOwnerShipResponse
    suspend fun delete(id: Long): Boolean
    suspend fun search(filter: CarSearchFilterRequest): List<CarDTO>
    suspend fun addImages(carId: Long, fileNames: List<String>): Boolean
    suspend fun findByUserId(userId: Long): List<CarDTO>
}
