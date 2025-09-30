package prof.entities

import kotlinx.datetime.LocalDateTime

data class TermDTO(
    var id: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val version: Int? = null,
    val active: Boolean? = null,
    val userId: Long? = null,
    var createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
)