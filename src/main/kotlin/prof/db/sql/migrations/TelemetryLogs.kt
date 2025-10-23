package prof.db.sql.migrations

import org.jetbrains.exposed.sql.Table

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