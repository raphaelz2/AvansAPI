package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import prof.enums.PowerSourceTypeEnum

@Serializable
class GetCarResponse(
    val id: Long,
    val make: String,
    val model: String,
    val price: Float,
    val pickupLocation: String,
    val category: String,
    val powerSourceType: PowerSourceTypeEnum,
    val imageFileNames: List<String>,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
