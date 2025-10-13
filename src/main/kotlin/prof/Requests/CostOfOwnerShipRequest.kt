package prof.Requests

import kotlinx.serialization.Serializable

@Serializable
data class CostOfOwnerShipRequest(
    val carId: Long,
    val kilometersPerYear: Int = 15000,
    val energyPricePerUnit: Double = 2.10 // € per liter (of per kWh)
)
