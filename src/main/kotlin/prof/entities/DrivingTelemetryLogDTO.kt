package prof.entities

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class DrivingTelemetryLogDTO(
    val tripId: Long,
    val userId: Long,
    val carId: Long,
    val timestamp: LocalDateTime,

    // basic trip metrics
    val tripDistanceKm: Double,
    val tripDurationMin: Int,

    // driving style / telemetry
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val harshBrakes: Int,
    val harshAccelerations: Int,
    val corneringScore: Int,   // 0..100
    val ecoScore: Int          // 0..100
)
