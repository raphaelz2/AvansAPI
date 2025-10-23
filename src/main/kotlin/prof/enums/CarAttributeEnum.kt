package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class CarAttributeEnum {
    MAKE,
    MODEL,
    PRICE,
    PICKUP_LOCATION,
    CATEGORY,
    POWER_SOURCE_TYPE,
    COLOR,
    ENGINE_TYPE,
    ENGINE_POWER,
    FUEL_TYPE,
    TRANSMISSION,
    INTERIOR_TYPE,
    INTERIOR_COLOR,
    EXTERIOR_TYPE,
    EXTERIOR_FINISH,
    WHEEL_SIZE,
    WHEEL_TYPE,
    SEATS,
    DOORS,
    MODEL_YEAR,
    LICENSE_PLATE,
    MILEAGE,
    VIN_NUMBER,
    TRADE_NAME,
    BPM,
    CURB_WEIGHT,
    MAX_WEIGHT,
    FIRST_REGISTRATION_DATE,
    COST_PER_KILOMETER,
    BOOKING_COST,
    DEPOSIT,
}

val carFieldToEnumMap = mapOf(
    "kenteken" to CarAttributeEnum.LICENSE_PLATE,
    "voertuigsoort" to CarAttributeEnum.CATEGORY,
    "merk" to CarAttributeEnum.MAKE,
    "handelsbenaming" to CarAttributeEnum.TRADE_NAME,
    "datum_tenaamstelling" to CarAttributeEnum.FIRST_REGISTRATION_DATE,
    "bruto_bpm" to CarAttributeEnum.BPM,
    "aantal_zitplaatsen" to CarAttributeEnum.SEATS,
    "eerste_kleur" to CarAttributeEnum.COLOR,
    "aantal_deuren" to CarAttributeEnum.DOORS,
    "cilinderinhoud" to CarAttributeEnum.ENGINE_POWER,
    "massa_ledig_voertuig" to CarAttributeEnum.CURB_WEIGHT,
    "toegestane_maximum_massa_voertuig" to CarAttributeEnum.MAX_WEIGHT,
    "datum_eerste_toelating" to CarAttributeEnum.FIRST_REGISTRATION_DATE,
    "catalogusprijs" to CarAttributeEnum.PRICE,
    "vermogen_massarijklaar" to CarAttributeEnum.ENGINE_POWER,
    "api_gekentekende_voertuigen_brandstof" to CarAttributeEnum.FUEL_TYPE,
    "api_gekentekende_voertuigen_carrosserie" to CarAttributeEnum.EXTERIOR_TYPE
)