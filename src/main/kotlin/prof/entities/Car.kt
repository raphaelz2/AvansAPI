package prof.entities

import kotlinx.datetime.LocalDateTime
import prof.enums.PowerSourceTypeEnum

class Car(
    id: Long,
    var imageFileNames: MutableList<String> = mutableListOf(),
    createdAt: LocalDateTime,
    modifiedAt: LocalDateTime,
    var attributes: MutableList<EntityAttribute> = mutableListOf()
) : BaseEntity(id, createdAt, modifiedAt) {

    fun getAttribute(attr: String): String? =
        attributes.find { it.attribute == attr }?.value

    fun getAttribute(attrEnum: prof.enums.CarAttributeEnum): String? =
        getAttribute(attrEnum.name)

    fun getAttributeFloat(attrEnum: prof.enums.CarAttributeEnum): Float =
        getAttribute(attrEnum)?.toFloatOrNull() ?: 0f

    fun <T : Enum<T>> getAttributeEnum(attrEnum: prof.enums.CarAttributeEnum, enumClass: Class<T>): T? =
        getAttribute(attrEnum)?.let { java.lang.Enum.valueOf(enumClass, it) }

}
