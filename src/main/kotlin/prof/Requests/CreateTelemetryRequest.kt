package prof.Requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateTelemetryRequest(
    val userId: Long? = null,
    val carId: Long? = null,
    // optional: allow client to pass metrics; if omitted we hardcode/sample
    val avgSpeedKmh: Double? = null,
    val maxSpeedKmh: Double? = null,
    val tripDistanceKm: Double? = null,
    val tripDurationMin: Int? = null,
    val harshBrakes: Int? = null,
    val harshAccelerations: Int? = null,
    val corneringScore: Int? = null,
    val ecoScore: Int? = null
)
