package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.PowerSourceType

class Car(
    id: Long,
    var make: String,
    var model: String,
    var price: Float,
    var pickupLocation: String,
    var category: String,
    var powerSourceType: PowerSourceType,
    var imageFileNames: MutableList<String> = mutableListOf(),
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
) : BaseEntity(id, createdAt, modifiedAt)
