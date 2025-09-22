package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import prof.enums.PowerSourceType

@Serializable
class CreateCarRequest(
    val make: String,
    val model: String,
    val price: Float,
    val pickupLocation: String,
    val category: String,
    val powerSourceType: PowerSourceType,
    val imageFileNames: MutableList<String> = mutableListOf(),
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
