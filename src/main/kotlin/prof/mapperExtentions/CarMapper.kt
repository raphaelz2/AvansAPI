package prof.mapperExtentions

import prof.entities.CarDTO
import prof.enums.CarAttributeEnum
import prof.enums.PowerSourceTypeEnum
import prof.responses.GetCarResponse
import prof.responses.GetCarsResponse

fun CarDTO.toGetCarResponse(baseUrl: String = "http://localhost:8080"): GetCarResponse = GetCarResponse(
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
    bookingCost = getAttribute(CarAttributeEnum.BOOKING_COST),
    costPerKilometer = getAttribute(CarAttributeEnum.COST_PER_KILOMETER),
    deposit = getAttribute(CarAttributeEnum.DEPOSIT),

    imageFileNames = this.imageFileNames.map { fileName ->
        "$baseUrl/uploads/cars/$fileName"
    },
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun List<CarDTO>.toGetCarResponseList(baseUrl: String = "http://localhost:8080"): List<GetCarResponse> =
    map { it.toGetCarResponse(baseUrl) }

fun List<CarDTO>.toGetCarsResponse(baseUrl: String = "http://localhost:8080"): GetCarsResponse =
    GetCarsResponse(
        GetCarResponseList = map { it.toGetCarResponse(baseUrl) }.toMutableList()
    )