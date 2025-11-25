package prof.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import prof.AuthenticatedUser
import prof.db.fake.FakeUserRepository
import prof.db.sql.SqlUserRepository
import prof.entities.LoginRequestDTO
import prof.responses.LoginResponse
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
                if (email != null && id != null && credential.payload.audience.contains(jwtAudience)) {
                    AuthenticatedUser(id, email)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }



    routing {
        //image
        static("/uploads") {
            files("uploads")
        }

        post("/login") {
            val request = call.receive<LoginRequestDTO>()
            val user = userRepo.findByEmail(request.email)
//            if (user == null || user.password != request.password) {
//                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
//                return@post
//            }
            val ok = user != null && prof.security.Passwords.verify(request.password,user.password)
            if(!ok){
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            val expirationTime = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)

            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("email", user.email)
                .withClaim("id", user.id)
                .withExpiresAt(Date(expirationTime))
                .sign(Algorithm.HMAC256(secret))
            println("Test test ${user.id}")
            call.respond(
                LoginResponse(
                    token = token,
                    email = user.email,
                    name = "${user.firstName} ${user.lastName}",
                    id = user.id!!
                )
            )
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
