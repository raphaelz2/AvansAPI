package prof.mapperExtentions

import prof.entities.Car
import prof.entities.Reservation
import prof.entities.User
import prof.responses.*
import prof.enums.CarAttributeEnum
import prof.enums.PowerSourceTypeEnum

/* ---------- Car mappings ---------- */

// Dynamische getters voor Car attributes
/** Map Car -> GetCarResponse */
fun Car.toGetCarResponse(): GetCarResponse = GetCarResponse(
    id = id,
    make = getAttribute(CarAttributeEnum.MAKE) ?: "",
    model = getAttribute(CarAttributeEnum.MODEL) ?: "",
    price = getAttributeFloat(CarAttributeEnum.PRICE),
    pickupLocation = getAttribute(CarAttributeEnum.PICKUP_LOCATION) ?: "",
    category = getAttribute(CarAttributeEnum.CATEGORY) ?: "",
    powerSourceType = getAttributeEnum(CarAttributeEnum.POWER_SOURCE_TYPE, PowerSourceTypeEnum::class.java) as? PowerSourceTypeEnum
        ?: PowerSourceTypeEnum.ICE,
    imageFileNames = imageFileNames.toList(),
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

/** List<Car> -> List<GetCarResponse> */
fun List<Car>.toGetCarResponseList(): List<GetCarResponse> =
    map { it.toGetCarResponse() }

fun List<Car>.toGetCarsResponse(): GetCarsResponse =
    GetCarsResponse(
        GetCarResponseList = map { it.toGetCarResponse() }.toMutableList()
    )

/* ---------- User mappings ---------- */

fun User.toGetUserResponse(): GetUserResponse = GetUserResponse(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun List<User>.toGetUserResponseList(): List<GetUserResponse> =
    map { it.toGetUserResponse() }

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

fun List<Reservation>.toGetReservationResponseList(): List<GetReservationResponse> =
    map { it.toGetReservationResponse() }

fun List<Reservation>.toGetReservationsResponse(): GetReservationsResponse =
    GetReservationsResponse(GetReservationsResponseList = map { it.toGetReservationResponse() }.toMutableList())
