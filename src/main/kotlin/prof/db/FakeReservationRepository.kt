package prof.db

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateReservationRequest
import prof.db.sql.Terms
import prof.entities.Reservation
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

object FakeReservationRepository : ReservationRepository {
    private var currentId: Long = 0L
    private val reservations = mutableListOf<Reservation>()

    init {
        // seed a few records
        runBlocking {
            create(
                CreateReservationRequest(
                    startTime = LocalDateTime(2024, 10, 15, 13, 0),
                    endTime   = LocalDateTime(2024, 10, 15, 15, 0),
                    userId = 1,
                    carId = 1,
                    termId = 1,
                    status = ReservationStatusEnum.CONFIRMED,
                    startMileage = 1,
                    endMileage = 2,
                    costPerKm = "2.00",
                    createdAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateReservationRequest(
                    startTime = LocalDateTime(2024, 10, 16, 9, 0),
                    endTime   = LocalDateTime(2024, 10, 16, 11, 0),
                    userId = 2,
                    carId = 2,
                    termId = 1,
                    status = ReservationStatusEnum.CONFIRMED,
                    startMileage = 1,
                    endMileage = 2,
                    costPerKm = "2.00",
                    createdAt = LocalDateTime(2024, 3, 27, 2, 16, 20),
                    modifiedAt = LocalDateTime(2024, 3, 27, 2, 16, 20)
                )
            )
        }
    }

    override suspend fun findReservationsForUser(userId: Long): List<Reservation> =
        reservations.filter { it.userId == userId }

    override suspend fun findAll(): List<Reservation> = reservations.toList()

    override suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean {
        return true
    }

    override suspend fun findById(id: Long): Reservation? = reservations.find { it.id == id }

    override suspend fun create(entity: CreateReservationRequest): Reservation {
        currentId++
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val reservation = Reservation(
            id = currentId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            userId = entity.userId,
            carId = entity.carId,
            termId = entity.termId,
            status = entity.status,
            startMileage = entity.startMileage,
            endMileage = entity.endMileage,
            costPerKm = BigDecimal(entity.costPerKm),
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )
        reservations.add(reservation)
        return reservation
    }

    override suspend fun update(entity: UpdateReservationRequest) {
        val idx = reservations.indexOfFirst { it.id == entity.id }
        if (idx >= 0) {
            reservations[idx] = Reservation(
                id = entity.id,
                startTime = entity.startTime,
                endTime = entity.endTime,
                userId = entity.userId,
                carId = entity.carId,
                termId = entity.termId,
                status = entity.status,
                startMileage = entity.startMileage,
                endMileage = entity.endMileage,
                costPerKm = entity.costPerKm,
                createdAt = entity.createdAt,
                modifiedAt = entity.modifiedAt
            )
        }
    }

    override suspend fun delete(id: Long): Boolean = reservations.removeIf { it.id == id }

    override suspend fun findReservationsForCar(carId: Long): List<Reservation> =
        reservations.filter { it.carId == carId }

    override suspend fun findReservationsForCarAndTimeframe(
        carId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation> =
        reservations.filter { it.carId == carId }
            .filter { it.startTime <= endTime && it.endTime >= startTime }
}
