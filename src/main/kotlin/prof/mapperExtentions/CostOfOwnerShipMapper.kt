package prof.mapperExtentions

import prof.entities.CarDTO
import prof.enums.CarAttributeEnum
import prof.enums.PowerSourceTypeEnum
import prof.responses.GetCostOfOwnerShipResponse
import java.math.BigDecimal
import java.math.RoundingMode

fun CarDTO.toGetCostOfOwnerShipResponse(): GetCostOfOwnerShipResponse {
    val defaultKmPerYear = 15000
    val defaultFuelPricePerLiter = 2.10

    val category = getAttribute(CarAttributeEnum.CATEGORY) ?: "Standard"

    val powerSource = getAttributeEnum(CarAttributeEnum.POWER_SOURCE_TYPE, PowerSourceTypeEnum::class.java)
        ?: PowerSourceTypeEnum.ICE

    val priceBd = getAttributeFloat(CarAttributeEnum.PRICE)?.toDouble()
        ?.let { BigDecimal.valueOf(it) } ?: BigDecimal.ZERO

    val avgConsumptionBd = when (powerSource) {
        PowerSourceTypeEnum.ICE -> BigDecimal("7.5")
        PowerSourceTypeEnum.HEV -> BigDecimal("5.0")
        PowerSourceTypeEnum.BEV -> BigDecimal("18.0")
        PowerSourceTypeEnum.FCEV -> BigDecimal("1.0")
    }

    val energyPriceBd = when (powerSource) {
        PowerSourceTypeEnum.BEV -> BigDecimal("0.25")
        PowerSourceTypeEnum.FCEV -> BigDecimal("12.0")
        else -> BigDecimal.valueOf(defaultFuelPricePerLiter)
    }

    val yearlyEnergyCostBd = BigDecimal.valueOf(defaultKmPerYear.toLong())
        .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
        .multiply(avgConsumptionBd)
        .multiply(energyPriceBd)
        .setScale(2, RoundingMode.HALF_UP)

    val yearlyMaintenanceBd = when (category.lowercase()) {
        "luxury" -> BigDecimal("1200.00")
        "suv" -> BigDecimal("900.00")
        "compact" -> BigDecimal("600.00")
        else -> BigDecimal("750.00")
    }

    val yearlyDepreciationBd = priceBd
        .multiply(BigDecimal("0.15"))
        .setScale(2, RoundingMode.HALF_UP)

    val totalBd = yearlyEnergyCostBd
        .add(yearlyMaintenanceBd)
        .add(yearlyDepreciationBd)
        .setScale(2, RoundingMode.HALF_UP)

    return GetCostOfOwnerShipResponse(
        carId = id,
        category = category,
        powerSourceType = powerSource.name,
        kilometersPerYear = defaultKmPerYear,
        energyPricePerUnit = energyPriceBd.toDouble(),
        averageConsumptionPer100Km = avgConsumptionBd.toDouble(),
        yearlyEnergyCost = yearlyEnergyCostBd.toDouble(),
        yearlyDepreciation = yearlyDepreciationBd.toDouble(),
        yearlyMaintenanceCost = yearlyMaintenanceBd.toDouble(),
        totalYearlyCost = totalBd.toDouble()
    )
}