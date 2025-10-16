package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationStatusEnum(val value: Int) {
    PENDING(1),
    CONFIRMED(2),
    CANCELLED(3);

    companion object {
        fun fromValue(value: Int): ReservationStatusEnum? =
            entries.find { it.value == value }
    }
}