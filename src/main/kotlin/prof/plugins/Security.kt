package prof.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.AuthenticatedUser
import prof.db.FakeUserRepository
import prof.db.sql.SqlUserRepository
import prof.entities.LoginRequest
import java.util.*

fun Application.configureSecurity() {
    val useFake = environment.config.propertyOrNull("app.useFake")?.getString()?.toBoolean() ?: true
    val userRepo = if (useFake) FakeUserRepository else SqlUserRepository()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.issuer").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val secret = environment.config.property("jwt.secret").getString()

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                val id = credential.payload.getClaim("id").asLong()
                if (email != null && id != null) AuthenticatedUser(id, email)

                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    routing {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = userRepo.findByEmail(request.email)
            if (user == null || user.password != request.password) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("email", user.email)
                .withExpiresAt(Date(System.currentTimeMillis() + 600_000))
                .sign(Algorithm.HMAC256(secret))

            call.respond(hashMapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()
                val user = email?.let { userRepo.findByEmail(it) }
                val firstName = user?.firstName
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, ${firstName ?: "user"}! Token expires in ${expiresAt ?: "unknown"} ms.")
            }
        }
    }
}
