package prof.db

import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateReservationRequest
import prof.entities.Reservation
import kotlinx.datetime.LocalDateTime

interface ReservationRepository {
    suspend fun findReservationsForUser(userId: Long): List<Reservation>
    suspend fun findAll(): List<Reservation>
    suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean
    suspend fun findById(id: Long): Reservation?
    suspend fun create(entity: CreateReservationRequest): Reservation
    suspend fun update(entity: UpdateReservationRequest)
    suspend fun delete(id: Long): Boolean
    suspend fun findReservationsForCar(carId: Long): List<Reservation>
    suspend fun findReservationsForCarAndTimeframe(carId: Long, startTime: LocalDateTime, endTime: LocalDateTime): List<Reservation>
}
