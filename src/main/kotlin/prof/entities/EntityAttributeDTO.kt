package prof.entities

import prof.enums.EntityEnum

class EntityAttributeDTO(
    var id: Long,
    var entity: EntityEnum,
    var entityId: Long,
    var attribute: String,
    var value: String,
    var createdAt: String? = null,
    var modifiedAt: String? = null,
)
