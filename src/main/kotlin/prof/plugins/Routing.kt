package prof.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.db.FakeCarRepository
import prof.db.FakeEntityAttributeRepository
import prof.db.FakeReservationRepository
import prof.db.FakeTelemetryRepository
import prof.db.FakeUserRepository
import prof.db.TelemetryRepository
import prof.db.sql.SqlCarRepository
import prof.db.sql.SqlReservationRepository
import prof.db.sql.SqlUserRepository
import prof.db.sql.DatabaseFactory
import prof.db.sql.SqlEntityAttributeRepository
import prof.db.sql.SqlTelemetryRepository
import prof.db.sql.SqlTermRepository
import prof.routes.TermRoute
import prof.routes.carRoutes
import prof.routes.reservationRoutes
import prof.routes.telemetryRoutes
import prof.routes.userRoutes

fun Application.configureRouting() {
    val useFake = environment.config.propertyOrNull("app.useFake")?.getString()?.toBoolean() ?: true

    val userRepo = if (useFake) FakeUserRepository else SqlUserRepository()
    val resRepo = if (useFake) FakeReservationRepository else SqlReservationRepository()
    val entRepo = if (useFake) FakeEntityAttributeRepository else SqlEntityAttributeRepository()
    val carRepo = if (useFake) FakeCarRepository else SqlCarRepository(entityAttributeRepo = entRepo as SqlEntityAttributeRepository)
    val telemetryRepo: TelemetryRepository = if (useFake) FakeTelemetryRepository else SqlTelemetryRepository()

    if (!useFake) {
        DatabaseFactory.init(environment)
    }

    routing {
        get("/") {
            call.respondText("Welcome to the car rental api! (mode=" + (if (useFake) "FAKE" else "SQL") + ")")
        }

        authenticate("auth-jwt") {
            userRoutes(userRepo)
            reservationRoutes(resRepo)
            carRoutes(carRepo)
            telemetryRoutes(telemetryRepo)
        }
    }

}
