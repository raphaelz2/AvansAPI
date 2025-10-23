package prof.db

import kotlinx.datetime.LocalDateTime
import prof.Requests.CreateTelemetryRequest
import prof.entities.DrivingTelemetryLogDTO

interface TelemetryRepositoryInterface {
    suspend fun create(req: CreateTelemetryRequest, defaultUserId: Long = 1L, defaultCarId: Long = 1L): DrivingTelemetryLogDTO
    suspend fun findAll(): List<DrivingTelemetryLogDTO>
    suspend fun findByTripId(tripId: Long): DrivingTelemetryLogDTO?
    suspend fun delete(tripId: Long): Boolean

    // optional filters
    suspend fun findForUser(userId: Long): List<DrivingTelemetryLogDTO>
    suspend fun findForCar(carId: Long): List<DrivingTelemetryLogDTO>
    suspend fun findForCarAndTimeframe(carId: Long, start: LocalDateTime, end: LocalDateTime): List<DrivingTelemetryLogDTO>
}
