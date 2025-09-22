package prof.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntity(
    val id: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime
)
