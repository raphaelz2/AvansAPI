package prof.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntity(
    var id: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime
)
