package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class CreateReservationRequest(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
