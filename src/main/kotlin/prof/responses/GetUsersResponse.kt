package prof.responses

import kotlinx.serialization.Serializable

@Serializable
class GetUsersResponse(
    var GetUsersResponseList: MutableList<GetUserResponse> = mutableListOf(),
)
