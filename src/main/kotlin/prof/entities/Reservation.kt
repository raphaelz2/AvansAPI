package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

class Reservation(
    id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    val termId: Long,
    val status: ReservationStatusEnum,
    val startMileage: Int,
    val endMileage: Int,
    val costPerKm: BigDecimal,
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
) : BaseEntity(id, createdAt, modifiedAt)
