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
    val status = integer("status")
    val termId = long("term_id").references(Terms.id, onDelete = ReferenceOption.CASCADE)
    val startMileage  = integer("start_mileage")
    val endMileage   = integer("end_mileage")
    val costPerKm = decimal("cost_per_km", precision = 10, scale = 2)
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

object TelemetryLogs : Table("telemetry_logs") {
    val tripId = long("trip_id").autoIncrement()
    val userId = long("user_id")
    val carId = long("car_id")
    val timestamp = varchar("timestamp", 40) // ISO string
    val tripDistanceKm = double("trip_distance_km")
    val tripDurationMin = integer("trip_duration_min")
    val avgSpeedKmh = double("avg_speed_kmh")
    val maxSpeedKmh = double("max_speed_kmh")
    val harshBrakes = integer("harsh_brakes")
    val harshAccelerations = integer("harsh_accelerations")
    val corneringScore = integer("cornering_score")
    val ecoScore = integer("eco_score")
    override val primaryKey = PrimaryKey(tripId)
}
