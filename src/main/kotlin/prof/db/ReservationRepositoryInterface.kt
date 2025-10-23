package prof.db

import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateReservationRequest
import prof.entities.ReservationDTO
import kotlinx.datetime.LocalDateTime

interface ReservationRepositoryInterface {
    suspend fun findReservationsForUser(userId: Long): List<ReservationDTO>
    suspend fun findAll(): List<ReservationDTO>
    suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean
    suspend fun findById(id: Long): ReservationDTO?
    suspend fun create(entity: CreateReservationRequest): ReservationDTO
    suspend fun update(entity: UpdateReservationRequest)
    suspend fun delete(id: Long): Boolean
    suspend fun findReservationsForCar(carId: Long): List<ReservationDTO>
    suspend fun findReservationsForCarAndTimeframe(carId: Long, startTime: LocalDateTime, endTime: LocalDateTime): List<ReservationDTO>
}
