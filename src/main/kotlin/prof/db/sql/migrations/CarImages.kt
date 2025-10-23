package prof.db.sql.migrations

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CarImages : Table("car_images") {
    val id = long("id").autoIncrement()
    val carId = long("car_id").references(Cars.id, onDelete = ReferenceOption.CASCADE)
    val filename = varchar("filename", 255)
    override val primaryKey = PrimaryKey(id)
}