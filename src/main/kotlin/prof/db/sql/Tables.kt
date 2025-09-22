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
    val make = varchar("make", 100)
    val model = varchar("model", 100)
    val price = float("price")
    val pickupLocation = varchar("pickup_location", 200)
    val category = varchar("category", 40)
    val powerSourceType = varchar("power_source_type", 20)
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
