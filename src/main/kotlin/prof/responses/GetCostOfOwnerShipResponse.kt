package prof.responses

import kotlinx.serialization.Serializable

@Serializable
data class GetCostOfOwnerShipResponse(
    val carId: Long? = null,
    val category: String,
    val powerSourceType: String,
    val kilometersPerYear: Int,
    val energyPricePerUnit: Double,
    val averageConsumptionPer100Km: Double,
    val yearlyEnergyCost: Double,
    val yearlyDepreciation: Double,
    val yearlyMaintenanceCost: Double,
    val totalYearlyCost: Double
)
