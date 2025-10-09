import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import prof.AuthenticatedUser
import kotlin.test.BeforeTest


open class BaseRouteTest {
    protected val inMemoryDb = InMemoryDatabase()

    protected lateinit var testUser: User
    protected val testUserId = 1L
    protected val testUserEmail = "test@example.com"

    @BeforeTest
    fun setupBase() {
        inMemoryDb.clear()

        testUser = User(
            id = testUserId,
            email = testUserEmail,
            passwordHash = "hashed_password"
        )

        inMemoryDb.users[testUserId] = testUser
    }


    protected fun testApp(
        configureRoutes: Routing.() -> Unit,
        test: suspend ApplicationTestBuilder.() -> Unit
    ) = testApplication {
        application {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    encodeDefaults = true
                })
            }

            // Authentication
            install(Authentication) {
                bearer("auth-bearer") {
                    authenticate { credential ->
                        AuthenticatedUser(
                            id = testUserId,
                            email = testUserEmail
                        )
                    }
                }
            }

            routing {
                authenticate("auth-bearer") {
                    configureRoutes()
                }
            }
        }
        test()
    }

    class InMemoryDatabase {
        val users = mutableMapOf<Long, User>()
        val terms = mutableMapOf<Long, Term>()

        private var termIdCounter = 1L
        private var userIdCounter = 1L

        fun clear() {
            users.clear()
            terms.clear()
            termIdCounter = 1L
            userIdCounter = 1L
        }

        fun createUser(email: String, passwordHash: String): User {
            val user = User(
                id = userIdCounter++,
                email = email,
                passwordHash = passwordHash
            )
            users[user.id] = user
            return user
        }

        fun findUserById(id: Long): User? = users[id]

        fun findUserByEmail(email: String): User? = users.values.find { it.email == email }

        fun createTerm(name: String, startDate: String, endDate: String, userId: Long): Term {
            val term = Term(
                id = termIdCounter++,
                name = name,
                startDate = startDate,
                endDate = endDate,
                userId = userId
            )
            terms[term.id] = term
            return term
        }

        fun findTermById(id: Long, userId: Long): Term? {
            return terms[id]?.takeIf { it.userId == userId }
        }

        fun findAllTerms(userId: Long): List<Term> {
            return terms.values.filter { it.userId == userId }
        }

        fun updateTerm(id: Long, name: String, startDate: String, endDate: String, userId: Long): Boolean {
            val term = terms[id]?.takeIf { it.userId == userId } ?: return false
            terms[id] = term.copy(name = name, startDate = startDate, endDate = endDate)
            return true
        }

        fun deleteTerm(id: Long, userId: Long): Boolean {
            val term = terms[id]?.takeIf { it.userId == userId } ?: return false
            terms.remove(id)
            return true
        }
    }

    data class User(
        val id: Long,
        val email: String,
        val passwordHash: String
    )

    data class Term(
        val id: Long,
        val name: String,
        val startDate: String,
        val endDate: String,
        val userId: Long
    )

}