package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.PowerSourceTypeEnum

class Car(
    id: Long,
    var imageFileNames: MutableList<String> = mutableListOf(),
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
) : BaseEntity(id, createdAt, modifiedAt)
