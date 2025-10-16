package prof.db

import prof.entities.EntityAttribute

object FakeEntityAttributeRepository : EntityAttributeRepository {
    private var currentId: Long = 0L
    private val attributes = mutableListOf<EntityAttribute>()

    override suspend fun findById(id: Long): EntityAttribute? =
        attributes.find { it.id == id }

    override suspend fun findByEntity(entity: String, entityId: Long): List<EntityAttribute> =
        attributes.filter { it.entity.name == entity && it.entityId == entityId }

    override suspend fun create(attribute: EntityAttribute): EntityAttribute =
        createBlocking(attribute)

    override suspend fun update(attribute: EntityAttribute): Int {
        val index = attributes.indexOfFirst { it.id == attribute.id }
        return if (index != -1) {
            attributes[index].apply {
                entity = attribute.entity
                entityId = attribute.entityId
                this.attribute = attribute.attribute
                value = attribute.value
                modifiedAt = attribute.modifiedAt
            }
            1
        } else 0
    }

    override suspend fun delete(id: Long): Boolean =
        attributes.removeIf { it.id == id }

    // ---- Blocking helpers ----
    fun findByEntityBlocking(entity: String, entityId: Long): List<EntityAttribute> =
        attributes.filter { it.entity.name == entity && it.entityId == entityId }

    fun createBlocking(attr: EntityAttribute): EntityAttribute {
        currentId++
        val newAttr = EntityAttribute(
            id = currentId,
            entity = attr.entity,
            entityId = attr.entityId,
            attribute = attr.attribute,
            value = attr.value,
            createdAt = attr.createdAt,
            modifiedAt = attr.modifiedAt
        )
        attributes.add(newAttr)
        return newAttr
    }

    fun updateBlocking(attr: EntityAttribute) {
        val index = attributes.indexOfFirst { it.id == attr.id }
        if (index != -1) {
            attributes[index].apply {
                entity = attr.entity
                entityId = attr.entityId
                attribute = attr.attribute
                value = attr.value
                modifiedAt = attr.modifiedAt
            }
        }
    }

    fun deleteBlocking(id: Long): Boolean =
        attributes.removeIf { it.id == id }
}
