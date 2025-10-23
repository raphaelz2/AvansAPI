package prof.db.sql.migrations

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Terms : Table("terms") {
    val id = long("id").autoIncrement()
    val title = varchar("title", 255)
    val content = text("content")
    val version = integer("version")
    val active = bool("active")
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = varchar("created_at", 40)
    val modifiedAt = varchar("modified_at", 40)
    override val primaryKey = PrimaryKey(id)
}