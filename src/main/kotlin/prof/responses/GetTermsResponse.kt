package prof.responses

import kotlinx.serialization.Serializable

@Serializable
class GetTermsResponse (
    var GetTermsResponseList: MutableList<GetTermResponse> = mutableListOf(),
)