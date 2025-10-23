package prof.mapperExtentions

import prof.entities.ReservationDTO
import prof.responses.GetReservationResponse
import prof.responses.GetReservationsResponse

fun ReservationDTO.toGetReservationResponse(): GetReservationResponse = GetReservationResponse(
    id = id,
    startTime = startTime,
    endTime = endTime,
    userId = userId,
    carId = carId,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun List<ReservationDTO>.toGetReservationResponseList(): List<GetReservationResponse> =
    map { it.toGetReservationResponse() }

fun List<ReservationDTO>.toGetReservationsResponse(): GetReservationsResponse =
    GetReservationsResponse(GetReservationsResponseList = map { it.toGetReservationResponse() }.toMutableList())