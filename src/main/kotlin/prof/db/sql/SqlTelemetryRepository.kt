package prof.db.sql

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateTelemetryRequest
import prof.db.TelemetryRepositoryInterface
import prof.db.sql.migrations.TelemetryLogs
import prof.entities.DrivingTelemetryLogDTO

class SqlTelemetryRepository : TelemetryRepositoryInterface {

    private fun rowToLog(row: ResultRow) = DrivingTelemetryLogDTO(
        tripId = row[TelemetryLogs.tripId],
        userId = row[TelemetryLogs.userId],
        carId = row[TelemetryLogs.carId],
        timestamp = LocalDateTime.parse(row[TelemetryLogs.timestamp]),
        tripDistanceKm = row[TelemetryLogs.tripDistanceKm],
        tripDurationMin = row[TelemetryLogs.tripDurationMin],
        avgSpeedKmh = row[TelemetryLogs.avgSpeedKmh],
        maxSpeedKmh = row[TelemetryLogs.maxSpeedKmh],
        harshBrakes = row[TelemetryLogs.harshBrakes],
        harshAccelerations = row[TelemetryLogs.harshAccelerations],
        corneringScore = row[TelemetryLogs.corneringScore],
        ecoScore = row[TelemetryLogs.ecoScore]
    )

    override suspend fun create(
        req: CreateTelemetryRequest,
        defaultUserId: Long,
        defaultCarId: Long
    ): DrivingTelemetryLogDTO = transaction {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        // NOTE: TelemetryLogs is a plain Table, so use: insert { } get TelemetryLogs.tripId
        val newId: Long = TelemetryLogs.insert { st ->
            st[userId] = req.userId ?: defaultUserId
            st[carId] = req.carId ?: defaultCarId
            st[timestamp] = now.toString()

            st[tripDistanceKm] = req.tripDistanceKm ?: 10.0
            st[tripDurationMin] = req.tripDurationMin ?: 15
            st[avgSpeedKmh] = req.avgSpeedKmh ?: 45.0
            st[maxSpeedKmh] = req.maxSpeedKmh ?: 110.0
            st[harshBrakes] = req.harshBrakes ?: 1
            st[harshAccelerations] = req.harshAccelerations ?: 1
            st[corneringScore] = req.corneringScore ?: 85
            st[ecoScore] = req.ecoScore ?: 80
        } get TelemetryLogs.tripId

        TelemetryLogs
            .selectAll()
            .where { TelemetryLogs.tripId eq newId }
            .single()
            .let(::rowToLog)
    }

    override suspend fun findAll(): List<DrivingTelemetryLogDTO> = transaction {
        TelemetryLogs.selectAll().map(::rowToLog)
    }

    override suspend fun findByTripId(tripId: Long): DrivingTelemetryLogDTO? = transaction {
        TelemetryLogs
            .selectAll()
            .where { TelemetryLogs.tripId eq tripId }
            .singleOrNull()
            ?.let(::rowToLog)
    }

    override suspend fun delete(tripId: Long): Boolean = transaction {
        TelemetryLogs.deleteWhere { TelemetryLogs.tripId eq tripId } > 0
    }

    override suspend fun findForUser(userId: Long): List<DrivingTelemetryLogDTO> = transaction {
        TelemetryLogs
            .selectAll()
            .where { TelemetryLogs.userId eq userId }
            .map(::rowToLog)
    }

    override suspend fun findForCar(carId: Long): List<DrivingTelemetryLogDTO> = transaction {
        TelemetryLogs
            .selectAll()
            .where { TelemetryLogs.carId eq carId }
            .map(::rowToLog)
    }

    override suspend fun findForCarAndTimeframe(
        carId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<DrivingTelemetryLogDTO> = transaction {
        val s = start.toString()
        val e = end.toString()
        TelemetryLogs
            .selectAll()
            .where {
                (TelemetryLogs.carId eq carId) and
                        (TelemetryLogs.timestamp greaterEq s) and
                        (TelemetryLogs.timestamp lessEq e)
            }
            .map(::rowToLog)
    }
}
