package prof.mapperExtentions

import prof.entities.Car
import prof.entities.Reservation
import prof.entities.User
import prof.responses.*

/* ---------- Car mappings ---------- */

fun Car.toGetCarResponse(): GetCarResponse = GetCarResponse(
    id = id,
    make = make,
    model = model,
    price = price,
    pickupLocation = pickupLocation,
    category = category,
    powerSourceType = powerSourceType,
    imageFileNames = imageFileNames.toList(),
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

/** List<Car> -> List<GetCarResponse> */
fun List<Car>.toGetCarResponseList(): List<GetCarResponse> =
    map { it.toGetCarResponse() }

/** List<Car> -> GetCarsResponse (what carRoutes.kt is importing/using) */
fun List<Car>.toGetCarsResponse(): GetCarsResponse =
    GetCarsResponse(GetCarResponseList = map { it.toGetCarResponse() }.toMutableList())


/* ---------- prof.security.User mappings ---------- */

fun User.toGetUserResponse(): GetUserResponse = GetUserResponse(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

/** List<prof.security.User> -> List<GetUserResponse> */
fun List<User>.toGetUserResponseList(): List<GetUserResponse> =
    map { it.toGetUserResponse() }

/** List<prof.security.User> -> GetUsersResponse (for userRoutes.kt) */
fun List<User>.toGetUsersResponse(): GetUsersResponse =
    GetUsersResponse(GetUsersResponseList = map { it.toGetUserResponse() }.toMutableList())


/* ---------- Reservation mappings ---------- */

fun Reservation.toGetReservationResponse(): GetReservationResponse = GetReservationResponse(
    id = id,
    startTime = startTime,
    endTime = endTime,
    userId = userId,
    carId = carId,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

/** List<Reservation> -> List<GetReservationResponse> */
fun List<Reservation>.toGetReservationResponseList(): List<GetReservationResponse> =
    map { it.toGetReservationResponse() }

/** List<Reservation> -> GetReservationsResponse (for reservationRoutes.kt) */
fun List<Reservation>.toGetReservationsResponse(): GetReservationsResponse =
    GetReservationsResponse(GetReservationsResponseList = map { it.toGetReservationResponse() }.toMutableList())
