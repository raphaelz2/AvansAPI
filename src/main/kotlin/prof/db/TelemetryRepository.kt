package prof.db

import kotlinx.datetime.LocalDateTime
import prof.Requests.CreateTelemetryRequest
import prof.entities.DrivingTelemetryLog

interface TelemetryRepository {
    suspend fun create(req: CreateTelemetryRequest, defaultUserId: Long = 1L, defaultCarId: Long = 1L): DrivingTelemetryLog
    suspend fun findAll(): List<DrivingTelemetryLog>
    suspend fun findByTripId(tripId: Long): DrivingTelemetryLog?
    suspend fun delete(tripId: Long): Boolean

    // optional filters
    suspend fun findForUser(userId: Long): List<DrivingTelemetryLog>
    suspend fun findForCar(carId: Long): List<DrivingTelemetryLog>
    suspend fun findForCarAndTimeframe(carId: Long, start: LocalDateTime, end: LocalDateTime): List<DrivingTelemetryLog>
}
