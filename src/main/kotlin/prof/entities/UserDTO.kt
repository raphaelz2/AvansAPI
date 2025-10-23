package prof.entities

import kotlinx.datetime.LocalDateTime

class UserDTO(
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    val password: String,
    var email: String,
    var createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
)
