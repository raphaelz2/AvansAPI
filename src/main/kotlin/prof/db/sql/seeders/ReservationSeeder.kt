package prof.db.sql.seeders

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateReservationRequest
import prof.db.sql.SqlReservationRepository
import prof.db.sql.migrations.Cars
import prof.db.sql.migrations.Reservations
import prof.db.sql.migrations.Terms
import prof.db.sql.migrations.Users
import prof.enums.ReservationStatusEnum

class ReservationSeeder(
    val reservationRepository: SqlReservationRepository = SqlReservationRepository()
) {
    suspend fun run() {
        if (transaction { Reservations.selectAll().empty() }) {
            println("✈️ ReservationSeeder start...")

            val users = transaction { Users.selectAll().map { it[Users.id] } }
            val terms = transaction { Terms.selectAll().map { it[Terms.id] } }
            val cars = transaction { Cars.selectAll().map { it[Cars.id] } }

            if (users.isEmpty() || terms.isEmpty() || cars.isEmpty()) {
                println("⚠️ Geen users, terms of cars gevonden. Seeder gestopt.")
                return
            }

            cars.forEachIndexed { carIndex, carId ->
                users.take(2).forEachIndexed { userIndex, userId ->
                    val reservations = listOf(
                        CreateReservationRequest(
                            startTime = LocalDateTime(2026, 1, 13, 9, 0),
                            endTime = LocalDateTime(2026, 1, 13, 12, 0),
                            userId = userId,
                            carId = carId,
                            termId = terms.random(),
                            status = ReservationStatusEnum.CONFIRMED,
                            startMileage = 10000 + (carIndex * 1000),
                            endMileage = 10050 + (carIndex * 1000),
                            costPerKm = "0.35"
                        ),
                        CreateReservationRequest(
                            startTime = LocalDateTime(2026, 1, 14, 14, 0),
                            endTime = LocalDateTime(2026, 1, 14, 18, 0),
                            userId = userId,
                            carId = carId,
                            termId = terms.random(),
                            status = ReservationStatusEnum.PENDING,
                            startMileage = 10050 + (carIndex * 1000),
                            endMileage = 10150 + (carIndex * 1000),
                            costPerKm = "0.35"
                        ),
                        CreateReservationRequest(
                            startTime = LocalDateTime(2026, 1, 15, 8, 0),
                            endTime = LocalDateTime(2026, 1, 15, 16, 0),
                            userId = userId,
                            carId = carId,
                            termId = terms.random(),
                            status = ReservationStatusEnum.CONFIRMED,
                            startMileage = 10150 + (carIndex * 1000),
                            endMileage = 10300 + (carIndex * 1000),
                            costPerKm = "0.35"
                        )
                    )

                    reservations.forEach { reservation ->
                        try {
                            reservationRepository.create(reservation)
                            println("✅ Reservation created for user $userId and car $carId (${reservation.status})")
                        } catch (e: Exception) {
                            println("⚠️ can reservation not add for user $userId and car $carId: ${e.message}")
                        }
                    }
                }
            }

            println("✅ ReservationSeeder finished.")
        } else {
            println("ℹ️ Reservations table contains al data from seeder.")
        }
    }
}