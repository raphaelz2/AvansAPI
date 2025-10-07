package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class CarAttributeEnum {
    MAKE,                       // Merk
    MODEL,                      // Niet aanwezig in NL lijst, kan optioneel
    PRICE,                      // Catalogusprijs
    PICKUP_LOCATION,            // Niet nodig voor basisinfo
    CATEGORY,                    // Voertuigsoort
    POWER_SOURCE_TYPE,           // Brandstof, elektrisch, hybride
    COLOR,                       // Eerste kleur
    ENGINE_TYPE,                 // Niet expliciet aanwezig
    ENGINE_POWER,                // Vermogen massarijklaar
    FUEL_TYPE,                   // Brandstof type
    TRANSMISSION,                // Niet aanwezig in NL lijst
    INTERIOR_TYPE,               // Niet aanwezig in NL lijst
    INTERIOR_COLOR,              // Niet aanwezig in NL lijst
    EXTERIOR_TYPE,               // Carrosserie
    EXTERIOR_FINISH,             // Niet aanwezig
    WHEEL_SIZE,                  // Niet aanwezig
    WHEEL_TYPE,                  // Niet aanwezig
    SEATS,                       // Aantal zitplaatsen
    DOORS,                       // Aantal deuren
    MODEL_YEAR,                  // Jaar eerste toelating
    LICENSE_PLATE,               // Kenteken
    MILEAGE,                     // Niet aanwezig
    VIN_NUMBER,                  // Niet aanwezig
    TRADE_NAME,                  // Handelsbenaming
    BPM,                         // Bruto BPM
    CURB_WEIGHT,                 // Massa ledig voertuig
    MAX_WEIGHT,                  // Toegestane max massa
    FIRST_REGISTRATION_DATE,     // Datum eerste toelating
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