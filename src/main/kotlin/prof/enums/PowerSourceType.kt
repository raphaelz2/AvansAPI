package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PowerSourceType {
    ICE,  // Internal Combustion Engine
    BEV,  // Battery Electric Vehicle
    FCEV, // Fuel Cell Electric Vehicle
    HEV   // Hybrid Electric Vehicle
}
