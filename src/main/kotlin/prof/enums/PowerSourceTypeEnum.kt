package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PowerSourceTypeEnum {
    ICE,  // Internal Combustion Engine
    BEV,  // Battery Electric Vehicle
    FCEV, // Fuel Cell Electric Vehicle
    HEV   // Hybrid Electric Vehicle
}
