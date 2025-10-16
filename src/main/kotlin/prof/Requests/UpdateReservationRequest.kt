package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

@Serializable
class UpdateReservationRequest(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val termId: Long,
    val status: ReservationStatusEnum,
    val startMileage: Int,
    val endMileage: Int,
    @Contextual val costPerKm: BigDecimal,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
