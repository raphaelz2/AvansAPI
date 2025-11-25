package prof.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val email: String,
    val name: String,
    val id: Long
)
