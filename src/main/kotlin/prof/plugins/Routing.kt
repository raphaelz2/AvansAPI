package prof.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.db.fake.FakeCarRepository
import prof.db.fake.FakeEntityAttributeRepository
import prof.db.fake.FakeReservationRepository
import prof.db.fake.FakeTelemetryRepository
import prof.db.fake.FakeUserRepository
import prof.db.TelemetryRepositoryInterface
import prof.db.TermRepositoryInterface
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
import prof.routes.userRegistrationRoutes

fun Application.configureRouting() {
    val useFake = environment.config.propertyOrNull("app.useFake")?.getString()?.toBoolean() ?: true

    val userRepo = if (useFake) FakeUserRepository else SqlUserRepository()
    val resRepo = if (useFake) FakeReservationRepository else SqlReservationRepository()
    val entRepo = if (useFake) FakeEntityAttributeRepository else SqlEntityAttributeRepository()
    val carRepo = if (useFake) FakeCarRepository else SqlCarRepository(entityAttributeRepo = entRepo as SqlEntityAttributeRepository)
    val telemetryRepo: TelemetryRepositoryInterface = if (useFake) FakeTelemetryRepository else SqlTelemetryRepository()
    val termRepository: TermRepositoryInterface = SqlTermRepository()

    if (!useFake) {
        DatabaseFactory.init(environment)
    }

    routing {
        get("/") {
            call.respondText("Welcome to the car rental api! (mode=" + (if (useFake) "FAKE" else "SQL") + ")")
        }

        // Public registration (no JWT required)
        // This enables the Android app to create new accounts without an existing token.
        userRegistrationRoutes(userRepo)

        authenticate("auth-jwt") {
            // All other user management endpoints remain protected.
            userRoutes(userRepo, includeCreate = false)
            reservationRoutes(resRepo)
            carRoutes(carRepo)
            telemetryRoutes(telemetryRepo)
            TermRoute(termRepository)
        }
    }
}
