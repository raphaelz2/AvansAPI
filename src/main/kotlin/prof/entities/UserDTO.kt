package prof.entities

import kotlinx.datetime.LocalDateTime

class UserDTO(
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    val password: String,
    var email: String,
    /** 0 = active, 1 = disabled */
    var disabled: Int = 0,
    var createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
)
