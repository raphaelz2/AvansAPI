package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class CreateTermRequest (
    val title: String,
    val content: String,
    val active: Boolean,
)