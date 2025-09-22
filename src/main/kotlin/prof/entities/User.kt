package prof.entities

import kotlinx.datetime.LocalDateTime

class User(
    id: Long,
    var firstName: String,
    var lastName: String,
    val password: String,
    var email: String,
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
) : BaseEntity(id, createdAt, modifiedAt)
