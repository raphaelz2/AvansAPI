package prof.db.sql

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateReservationRequest
import prof.Requests.UpdateReservationRequest
import prof.db.ReservationRepositoryInterface
import prof.db.sql.migrations.Reservations
import prof.entities.ReservationDTO
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

class SqlReservationRepository : ReservationRepositoryInterface {

    private fun rowToReservation(row: ResultRow) = ReservationDTO(
        id = row[Reservations.id],
        startTime = LocalDateTime.parse(row[Reservations.startTime]),
        endTime = LocalDateTime.parse(row[Reservations.endTime]),
        userId = row[Reservations.userId],
        carId = row[Reservations.carId],
        status = ReservationStatusEnum.fromValue(row[Reservations.status])
            ?: error("Unknown status value: ${row[Reservations.status]}"),
        termId = row[Reservations.termId],
        startMileage = row[Reservations.startMileage],
        endMileage = row[Reservations.endMileage],
        costPerKm = row[Reservations.costPerKm],
        createdAt = LocalDateTime.parse(row[Reservations.createdAt]),
        modifiedAt = LocalDateTime.parse(row[Reservations.modifiedAt])
    )

    override suspend fun findById(id: Long): ReservationDTO? = transaction {
        Reservations.selectAll().where { Reservations.id eq id }.singleOrNull()?.let { rowToReservation(it) }
    }

    override suspend fun findAll(): List<ReservationDTO> = transaction {
        Reservations.selectAll().map { rowToReservation(it) }
    }

    override suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean = transaction {
        val start = entity.startTime.toString()
        val end = entity.endTime.toString()

        val overlappingReservations = Reservations.selectAll()
            .where {
                (Reservations.startTime lessEq end) and
                (Reservations.endTime greaterEq start) and
                (Reservations.carId eq entity.carId) and
                (Reservations.status eq ReservationStatusEnum.CONFIRMED.value)
            }
            .count()

        overlappingReservations == 0L
    }

    override suspend fun create(entity: CreateReservationRequest): ReservationDTO = transaction {
        val newId: Long = Reservations.insert { st ->
            st[startTime] = entity.startTime.toString()
            st[endTime] = entity.endTime.toString()
            st[userId] = entity.userId
            st[carId] = entity.carId
            st[termId] = entity.termId
            st[status] = entity.status.value
            st[startMileage] = entity.startMileage
            st[endMileage] = entity.endMileage
            st[costPerKm] = BigDecimal(entity.costPerKm)
            st[createdAt] = Clock.System.now().toString()
            st[modifiedAt] = Clock.System.now().toString()
        } get Reservations.id
        Reservations.selectAll().where { Reservations.id eq newId }.single().let { rowToReservation(it) }
    }

    override suspend fun update(entity: UpdateReservationRequest) {
        transaction {
            Reservations.update({ Reservations.id eq entity.id }) { st ->
                st[startTime] = entity.startTime.toString()
                st[endTime] = entity.endTime.toString()
                st[userId] = entity.userId
                st[carId] = entity.carId
                st[termId] = entity.termId
                st[status] = entity.status.value
                st[startMileage] = entity.startMileage
                st[endMileage] = entity.endMileage
                st[costPerKm] = entity.costPerKm
                st[modifiedAt] = Clock.System.now().toString()
            }
        }
    }

    override suspend fun delete(id: Long): Boolean = transaction {
        Reservations.deleteWhere { Reservations.id eq id } > 0
    }

    override suspend fun findReservationsForUser(userId: Long): List<ReservationDTO> = transaction {
        Reservations.selectAll().where { Reservations.userId eq userId }.map { rowToReservation(it) }
    }

    override suspend fun findReservationsForCar(carId: Long): List<ReservationDTO> = transaction {
        Reservations.selectAll().where { Reservations.carId eq carId }.map { rowToReservation(it) }
    }

    override suspend fun findReservationsForCarAndTimeframe(
        carId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<ReservationDTO> = transaction {
        val s = startTime.toString()
        val e = endTime.toString()
        Reservations
            .selectAll()
            .where { (Reservations.carId eq carId) and (Reservations.startTime lessEq e) and (Reservations.endTime greaterEq s) }
            .map { rowToReservation(it) }
    }
}
