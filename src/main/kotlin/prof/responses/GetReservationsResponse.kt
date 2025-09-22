package prof.responses

import kotlinx.serialization.Serializable

@Serializable
class GetReservationsResponse(
    var GetReservationsResponseList: MutableList<GetReservationResponse> = mutableListOf(),
)
