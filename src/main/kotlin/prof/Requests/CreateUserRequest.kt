package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val password: String,
    val email: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)
