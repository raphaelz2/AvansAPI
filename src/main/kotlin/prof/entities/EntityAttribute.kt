package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.EntityEnum

class EntityAttribute(
    id: Long,
    var entity: EntityEnum,   // bv. CAR
    var entityId: Long,       // bv. Car.id
    var attribute: String,    // bv. "1 : enum Make // "
    var value: String,        // bv. "Tesla"
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime
) : BaseEntity(id, createdAt, modifiedAt)
