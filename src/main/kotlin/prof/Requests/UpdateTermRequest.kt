package prof.Requests

import kotlinx.serialization.Serializable

@Serializable
class UpdateTermRequest (
    val id: Long,
    val title: String,
    val content: String,
    val active: Boolean,
)