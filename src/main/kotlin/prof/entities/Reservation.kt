package prof.entities

import kotlinx.datetime.LocalDateTime

class Reservation(
    id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val userId: Long,
    val carId: Long,
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
) : BaseEntity(id, createdAt, modifiedAt)
