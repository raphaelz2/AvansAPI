package prof.responses

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import prof.enums.PowerSourceTypeEnum

@Serializable
data class GetCarResponse(
    val id: Long,
    val make: String,
    val model: String? = null,
    val price: Float? = null,
    val pickupLocation: String? = null,
    val category: String,
    val powerSourceType: PowerSourceTypeEnum,
    val color: String? = null,
    val engineType: String? = null,
    val enginePower: String? = null,
    val fuelType: String? = null,
    val transmission: String? = null,
    val interiorType: String? = null,
    val interiorColor: String? = null,
    val exteriorType: String? = null,
    val exteriorFinish: String? = null,
    val wheelSize: String? = null,
    val wheelType: String? = null,
    val seats: Int? = null,
    val doors: Int? = null,
    val modelYear: Int? = null,
    val licensePlate: String? = null,
    val mileage: Int? = null,
    val vinNumber: String? = null,
    val tradeName: String? = null,
    val bpm: Float? = null,
    val curbWeight: Int? = null,
    val maxWeight: Int? = null,
    val firstRegistrationDate: String? = null,
    val imageFileNames: List<String>,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
)