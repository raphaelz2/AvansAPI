package prof.responses

import kotlinx.serialization.Serializable

@Serializable
class GetCarsResponse(
    var GetCarResponseList: MutableList<GetCarResponse> = mutableListOf(),
)
