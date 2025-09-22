package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class GetReservationResponse(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
