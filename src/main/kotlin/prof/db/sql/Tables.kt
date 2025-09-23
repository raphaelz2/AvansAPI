package prof.db.sql

import org.jetbrains.exposed.sql.ReferenceOption
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

object Cars : Table("cars") {
    val id = long("id").autoIncrement()
    val createdAt = varchar("created_at", 40)
    val modifiedAt = varchar("modified_at", 40)
    override val primaryKey = PrimaryKey(id)
}

object CarImages : Table("car_images") {
    val id = long("id").autoIncrement()
    val carId = long("car_id").references(Cars.id, onDelete = ReferenceOption.CASCADE)
    val filename = varchar("filename", 255)
    override val primaryKey = PrimaryKey(id)
}

object Reservations : Table("reservations") {
    val id = long("id").autoIncrement()
    val startTime = varchar("start_time", 40)
    val endTime = varchar("end_time", 40)
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val carId = long("car_id").references(Cars.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = varchar("created_at", 40)
    val modifiedAt = varchar("modified_at", 40)
    override val primaryKey = PrimaryKey(id)
}

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

