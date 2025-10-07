package prof.db

import prof.entities.EntityAttribute

interface EntityAttributeRepository {
    suspend fun findById(id: Long): EntityAttribute?
    suspend fun findByEntity(entity: String, entityId: Long): List<EntityAttribute>
    suspend fun create(attribute: EntityAttribute): EntityAttribute
    suspend fun update(attribute: EntityAttribute): Int
    suspend fun delete(id: Long): Boolean
}
