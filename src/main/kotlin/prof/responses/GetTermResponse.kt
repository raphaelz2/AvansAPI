package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetTermResponse(
    val id: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val version: Int? = null,
    val active: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
)