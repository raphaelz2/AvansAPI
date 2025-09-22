package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class GetUserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
