package prof.db.sql

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import prof.db.EntityAttributeRepositoryInterface
import prof.db.sql.migrations.EntityAttributes
import prof.entities.EntityAttributeDTO
import prof.enums.EntityEnum

class SqlEntityAttributeRepository : EntityAttributeRepositoryInterface {

    private fun rowToEntityAttribute(row: ResultRow): EntityAttributeDTO {
        return EntityAttributeDTO(
            id = row[EntityAttributes.id],
            entity = EntityEnum.valueOf(row[EntityAttributes.entity]),
            entityId = row[EntityAttributes.entityId],
            attribute = row[EntityAttributes.attribute],
            value = row[EntityAttributes.value],
            createdAt = row[EntityAttributes.createdAt],
            modifiedAt = row[EntityAttributes.modifiedAt]
        )
    }

    override suspend fun findById(id: Long): EntityAttributeDTO? = transaction {
        EntityAttributes.selectAll().where { EntityAttributes.id eq id }
            .singleOrNull()
            ?.let { rowToEntityAttribute(it) }
    }

    override suspend fun findByEntity(entity: String, entityId: Long): List<EntityAttributeDTO> = transaction {
        EntityAttributes.selectAll()
            .where { (EntityAttributes.entity eq entity) and (EntityAttributes.entityId eq entityId) }
            .map { rowToEntityAttribute(it) }
    }

    override suspend fun create(attribute: EntityAttributeDTO): EntityAttributeDTO = transaction {
        val newId: Long = EntityAttributes.insert { st ->
            st[entity] = attribute.entity.name
            st[entityId] = attribute.entityId
            st[EntityAttributes.attribute] = attribute.attribute
            st[value] = attribute.value
            st[createdAt] = attribute.createdAt.toString()
            st[modifiedAt] = attribute.modifiedAt.toString()
        } get EntityAttributes.id

        EntityAttributes.selectAll()
            .where { EntityAttributes.id eq newId }
            .single()
            .let { rowToEntityAttribute(it) }
    }

    override suspend fun update(attribute: EntityAttributeDTO): Int = transaction {
        EntityAttributes.update({ EntityAttributes.id eq attribute.id }) { st ->
            st[entity] = attribute.entity.name
            st[entityId] = attribute.entityId
            st[EntityAttributes.attribute] = attribute.attribute
            st[value] = attribute.value
            st[modifiedAt] = attribute.modifiedAt.toString()
        }
    }

    override suspend fun delete(id: Long): Boolean = transaction {
        EntityAttributes.deleteWhere { EntityAttributes.id eq id } > 0
    }

    fun findByEntityBlocking(entity: String, entityId: Long): List<EntityAttributeDTO> = transaction {
        EntityAttributes.selectAll()
            .where { (EntityAttributes.entity eq entity) and (EntityAttributes.entityId eq entityId) }
            .map { rowToEntityAttribute(it) }
    }

    fun createBlocking(attribute: EntityAttributeDTO): EntityAttributeDTO = transaction {
        val newId: Long = EntityAttributes.insert { st ->
            st[entity] = attribute.entity.name
            st[entityId] = attribute.entityId
            st[EntityAttributes.attribute] = attribute.attribute
            st[value] = attribute.value
            st[createdAt] = attribute.createdAt.toString()
            st[modifiedAt] = attribute.modifiedAt.toString()
        } get EntityAttributes.id

        EntityAttributes.selectAll()
            .where { EntityAttributes.id eq newId }
            .single()
            .let { rowToEntityAttribute(it) }
    }

    fun updateBlocking(attribute: EntityAttributeDTO) = transaction {
        EntityAttributes.update({ EntityAttributes.id eq attribute.id }) { st ->
            st[entity] = attribute.entity.name
            st[entityId] = attribute.entityId
            st[EntityAttributes.attribute] = attribute.attribute
            st[value] = attribute.value
            st[modifiedAt] = attribute.modifiedAt.toString()
        }
    }

    fun deleteBlocking(id: Long): Boolean = transaction {
        EntityAttributes.deleteWhere { EntityAttributes.id eq id } > 0
    }
}
