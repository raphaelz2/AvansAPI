package prof.db.sql.migrations

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = long("id").autoIncrement()
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val password = varchar("password", 255)
    val email = varchar("email", 200).uniqueIndex()
    val createdAt = varchar("created_at", 40)
    val modifiedAt = varchar("modified_at", 40)
    override val primaryKey = PrimaryKey(id)
}