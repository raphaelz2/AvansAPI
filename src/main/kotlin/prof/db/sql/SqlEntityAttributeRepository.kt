package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.db.EntityAttributeRepository
import prof.entities.EntityAttribute
import prof.enums.EntityEnum

class SqlEntityAttributeRepository : EntityAttributeRepository {

    private fun rowToEntityAttribute(row: ResultRow): EntityAttribute {
        return EntityAttribute(
            id = row[EntityAttributes.id],
            entity = EntityEnum.valueOf(row[EntityAttributes.entity]),
            entityId = row[EntityAttributes.entityId],
            attribute = row[EntityAttributes.attribute],
            value = row[EntityAttributes.value],
            createdAt = LocalDateTime.parse(row[EntityAttributes.createdAt]),
            modifiedAt = LocalDateTime.parse(row[EntityAttributes.modifiedAt])
        )
    }

    override suspend fun findById(id: Long): EntityAttribute? = transaction {
        EntityAttributes
            .selectAll()
            .where { EntityAttributes.id eq id }
            .singleOrNull()
            ?.let { rowToEntityAttribute(it) }
    }

    override suspend fun findByEntity(entity: String, entityId: Long): List<EntityAttribute> = transaction {
        EntityAttributes
            .selectAll()
            .where { (EntityAttributes.entity eq entity) and (EntityAttributes.entityId eq entityId) }
            .map { rowToEntityAttribute(it) }
    }

    override suspend fun create(attribute: EntityAttribute): EntityAttribute = transaction {
        val newId: Long = EntityAttributes.insert { st ->
            st[entity] = attribute.entity.name
            st[entityId] = attribute.entityId
            st[EntityAttributes.attribute] = attribute.attribute
            st[value] = attribute.value
            st[createdAt] = attribute.createdAt.toString()
            st[modifiedAt] = attribute.modifiedAt.toString()
        } get EntityAttributes.id

        // fetch created row en map
        EntityAttributes
            .selectAll()
            .where { EntityAttributes.id eq newId }
            .single()
            .let { rowToEntityAttribute(it) }
    }

    override suspend fun update(attribute: EntityAttribute) = transaction {
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
}
