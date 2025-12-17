package prof.entities

class CarDTO(
    var id: Long,
    var imageFileNames: MutableList<String> = mutableListOf(),
    var userId: Long,
    var createdAt: String? = null,
    var modifiedAt: String? = null,
    var attributes: MutableList<EntityAttributeDTO> = mutableListOf()
){

    fun getAttribute(attr: String): String? =
        attributes.find { it.attribute == attr }?.value

    fun getAttribute(attrEnum: prof.enums.CarAttributeEnum): String? =
        getAttribute(attrEnum.name)

    fun getAttributeInt(attrEnum: prof.enums.CarAttributeEnum): Int? =
        getAttribute(attrEnum)?.toIntOrNull()

    fun getAttributeFloat(attrEnum: prof.enums.CarAttributeEnum): Float? =
        getAttribute(attrEnum)?.toFloatOrNull()

    fun <T : Enum<T>> getAttributeEnum(attrEnum: prof.enums.CarAttributeEnum, enumClass: Class<T>): T? =
        getAttribute(attrEnum)?.let { java.lang.Enum.valueOf(enumClass, it) }

}