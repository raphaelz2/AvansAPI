package prof.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ColorEnum(val hexCode: String) {
    RED("#FF0000"),
    YELLOW("#FFFF00"),
    BLUE("#0000FF"),
    GREEN("#008000"),
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    SILVER("#C0C0C0"),
    GRAY("#808080"),
    ORANGE("#FFA500"),
    PURPLE("#800080"),
    BROWN("#8B4513");

    fun toHex(): String = hexCode
}
