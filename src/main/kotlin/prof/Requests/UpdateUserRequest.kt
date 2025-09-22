package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class UpdateUserRequest(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
