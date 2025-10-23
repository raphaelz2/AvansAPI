package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class GetReservationResponse(
    val id: Long? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
)
