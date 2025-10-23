package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

class ReservationDTO(
    var id: Long? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val termId: Long,
    val status: ReservationStatusEnum,
    val startMileage: Int,
    val endMileage: Int,
    val costPerKm: BigDecimal,
    var createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
)
