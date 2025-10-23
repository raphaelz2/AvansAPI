package prof.db.sql.migrations

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EntityAttributes : Table("entity_attributes") {
    val id = long("id").autoIncrement()
    val entity = varchar("entity", 50)
    val entityId = long("entity_id")
    val attribute = varchar("attribute", 100)
    val value = text("value")
    val createdAt = varchar("created_at", 50)
    val modifiedAt = varchar("modified_at", 50)

    override val primaryKey = PrimaryKey(id)
}