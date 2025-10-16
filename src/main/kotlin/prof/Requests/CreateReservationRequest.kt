package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import prof.enums.ReservationStatusEnum

@Serializable
data class CreateReservationRequest(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val termId: Long,
    val status: ReservationStatusEnum,
    val startMileage: Int,
    val endMileage: Int,
    val costPerKm: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
)
