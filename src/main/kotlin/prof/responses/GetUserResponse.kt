package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class GetUserResponse(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    /** 0 = active, 1 = disabled */
    val disabled: Int = 0,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
)
