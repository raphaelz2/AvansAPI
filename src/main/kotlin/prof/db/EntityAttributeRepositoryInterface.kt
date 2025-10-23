package prof.db

import prof.entities.EntityAttributeDTO

interface EntityAttributeRepositoryInterface {
    suspend fun findById(id: Long): EntityAttributeDTO?
    suspend fun findByEntity(entity: String, entityId: Long): List<EntityAttributeDTO>
    suspend fun create(attribute: EntityAttributeDTO): EntityAttributeDTO
    suspend fun update(attribute: EntityAttributeDTO): Int
    suspend fun delete(id: Long): Boolean
}
