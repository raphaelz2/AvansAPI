package prof.db.sql.migrations

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Reservations : Table("reservations") {
    val id = long("id").autoIncrement()
    val startTime = varchar("start_time", 40)
    val endTime = varchar("end_time", 40)
    val userId = long("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val carId = long("car_id").references(Cars.id, onDelete = ReferenceOption.CASCADE)
    val status = integer("status")
    val termId = long("term_id").references(Terms.id, onDelete = ReferenceOption.CASCADE)
    val startMileage  = integer("start_mileage")
    val endMileage   = integer("end_mileage")
    val costPerKm = decimal("cost_per_km", precision = 10, scale = 2)
    val createdAt = varchar("created_at", 40)
    val modifiedAt = varchar("modified_at", 40)
    override val primaryKey = PrimaryKey(id)
}