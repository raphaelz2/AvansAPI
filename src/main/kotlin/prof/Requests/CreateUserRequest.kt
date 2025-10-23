package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val firstName: String,
    val lastName: String,
    val password: String,
    val email: String,
)
