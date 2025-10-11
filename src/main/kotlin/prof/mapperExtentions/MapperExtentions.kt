package prof.mapperExtentions

import prof.entities.Car
import prof.entities.Reservation
import prof.entities.User
import prof.responses.*
import prof.enums.CarAttributeEnum
import prof.enums.PowerSourceTypeEnum

/* ---------- Car mappings ---------- */

/** Map Car -> GetCarResponse */
fun Car.toGetCarResponse(): GetCarResponse = GetCarResponse(
    id = id,
    make = getAttribute(CarAttributeEnum.MAKE) ?: "",
    model = getAttribute(CarAttributeEnum.MODEL),
    price = getAttributeFloat(CarAttributeEnum.PRICE),
    pickupLocation = getAttribute(CarAttributeEnum.PICKUP_LOCATION),
    category = getAttribute(CarAttributeEnum.CATEGORY) ?: "",
    powerSourceType = getAttributeEnum(CarAttributeEnum.POWER_SOURCE_TYPE, PowerSourceTypeEnum::class.java) as? PowerSourceTypeEnum
        ?: PowerSourceTypeEnum.ICE,
    color = getAttribute(CarAttributeEnum.COLOR),
    engineType = getAttribute(CarAttributeEnum.ENGINE_TYPE),
    enginePower = getAttribute(CarAttributeEnum.ENGINE_POWER),
    fuelType = getAttribute(CarAttributeEnum.FUEL_TYPE),
    transmission = getAttribute(CarAttributeEnum.TRANSMISSION),
    interiorType = getAttribute(CarAttributeEnum.INTERIOR_TYPE),
    interiorColor = getAttribute(CarAttributeEnum.INTERIOR_COLOR),
    exteriorType = getAttribute(CarAttributeEnum.EXTERIOR_TYPE),
    exteriorFinish = getAttribute(CarAttributeEnum.EXTERIOR_FINISH),
    wheelSize = getAttribute(CarAttributeEnum.WHEEL_SIZE),
    wheelType = getAttribute(CarAttributeEnum.WHEEL_TYPE),
    seats = getAttributeInt(CarAttributeEnum.SEATS),
    doors = getAttributeInt(CarAttributeEnum.DOORS),
    modelYear = getAttributeInt(CarAttributeEnum.MODEL_YEAR),
    licensePlate = getAttribute(CarAttributeEnum.LICENSE_PLATE),
    mileage = getAttributeInt(CarAttributeEnum.MILEAGE),
    vinNumber = getAttribute(CarAttributeEnum.VIN_NUMBER),
    tradeName = getAttribute(CarAttributeEnum.TRADE_NAME),
    bpm = getAttributeFloat(CarAttributeEnum.BPM),
    curbWeight = getAttributeInt(CarAttributeEnum.CURB_WEIGHT),
    maxWeight = getAttributeInt(CarAttributeEnum.MAX_WEIGHT),
    firstRegistrationDate = getAttribute(CarAttributeEnum.FIRST_REGISTRATION_DATE),
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