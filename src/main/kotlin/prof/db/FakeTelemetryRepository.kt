package prof.db

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import prof.Requests.CreateTelemetryRequest
import prof.db.TelemetryRepository
import prof.entities.DrivingTelemetryLog

object FakeTelemetryRepository : TelemetryRepository {
    private val logs = mutableListOf<DrivingTelemetryLog>()
    private var currentId = 0L

    override suspend fun create(req: CreateTelemetryRequest, defaultUserId: Long, defaultCarId: Long): DrivingTelemetryLog {
        fun r(min: Int, max: Int) = Random.nextInt(min, max + 1)
        fun rd(min: Double, max: Double) = Random.nextDouble(min, max)

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        currentId++

        val log = DrivingTelemetryLog(
            tripId = currentId,
            userId = req.userId ?: defaultUserId,
            carId = req.carId ?: defaultCarId,
            timestamp = now,
            tripDistanceKm  = req.tripDistanceKm  ?: rd(3.0, 28.0),
            tripDurationMin = req.tripDurationMin ?: r(5, 60),
            avgSpeedKmh     = req.avgSpeedKmh     ?: rd(25.0, 65.0),
            maxSpeedKmh     = req.maxSpeedKmh     ?: rd(60.0, 130.0),
            harshBrakes     = req.harshBrakes     ?: r(0, 5),
            harshAccelerations = req.harshAccelerations ?: r(0, 5),
            corneringScore  = req.corneringScore  ?: r(60, 95),
            ecoScore        = req.ecoScore        ?: r(55, 98)
        )
        logs += log
        return log
    }

    override suspend fun findAll(): List<DrivingTelemetryLog> = logs.toList()
    override suspend fun findByTripId(tripId: Long): DrivingTelemetryLog? = logs.find { it.tripId == tripId }
    override suspend fun delete(tripId: Long): Boolean = logs.removeIf { it.tripId == tripId }
    override suspend fun findForUser(userId: Long) = logs.filter { it.userId == userId }
    override suspend fun findForCar(carId: Long) = logs.filter { it.carId == carId }
    override suspend fun findForCarAndTimeframe(carId: Long, start: LocalDateTime, end: LocalDateTime) =
        logs.filter { it.carId == carId && it.timestamp >= start && it.timestamp <= end }
}
