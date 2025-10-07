package prof.Requests

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import prof.enums.PowerSourceTypeEnum

@Serializable
class UpdateCarRequest(
    val id: Long,
    val make: String,
    val model: String?,
    val price: Float?,
    val pickupLocation: String?,
    val category: String,
    val powerSourceType: PowerSourceTypeEnum,
    val color: String?,
    val engineType: String?,
    val enginePower: String?,
    val fuelType: String?,
    val transmission: String?,
    val interiorType: String?,
    val interiorColor: String?,
    val exteriorType: String?,
    val exteriorFinish: String?,
    val wheelSize: String?,
    val wheelType: String?,
    val seats: Int?,
    val doors: Int?,
    val modelYear: Int?,
    val licensePlate: String?,
    val mileage: Int?,
    val vinNumber: String?,
    val tradeName: String?,
    val bpm: Float?,
    val curbWeight: Int?,
    val maxWeight: Int?,
    val firstRegistrationDate: String?,
    val imageFileNames: MutableList<String> = mutableListOf(),
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
)
