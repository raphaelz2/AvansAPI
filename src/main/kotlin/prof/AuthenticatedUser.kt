package prof

import io.ktor.server.auth.*

data class AuthenticatedUser(
    val id: Long,
    val email: String
) : Principal