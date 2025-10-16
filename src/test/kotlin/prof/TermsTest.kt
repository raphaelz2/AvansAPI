package prof

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.*
import prof.routes.TermRoute
import prof.db.TermRepositoryInterface
import prof.Requests.CreateTermRequest
import prof.Requests.UpdateTermRequest
import prof.entities.TermDTO

class TermsTest {

    private fun ApplicationTestBuilder.setupTestApplication(repository: TermRepositoryInterface) {
        application {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Authentication) {
                bearer("auth-bearer") {
                    authenticate { credential ->
                        AuthenticatedUser(id = 1L, email = "test@example.com")
                    }
                }
            }

            routing {
                authenticate("auth-bearer") {
                    TermRoute(repository)
                }
            }
        }
    }

    @Test
    fun `GET all terms returns OK with term list`() = testApplication {
        val mockRepository = MockTermRepository(
            findAllResult = listOf(
                TermDTO(id = 1, userId = 1, title = "Term 1", content = "Term 1 Content"),
                TermDTO(id = 2, userId = 1, title = "Term 2", content = "Term 2 Content"),
            )
        )

        setupTestApplication(mockRepository)

        client.get("/terms") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(mockRepository.findAllCalled)
        }
    }

    @Test
    fun `GET term by id returns OK when term exists`() = testApplication {
        val mockRepository = MockTermRepository(
            findByIdResult = TermDTO(id = 1, userId = 1, title = "Term 1", content = "Term 1 Content"),
            )

        setupTestApplication(mockRepository)

       client.get("/terms/1") {
           bearerAuth("test-token")
       }.apply {
           println("Status: $status")
           println("Body: ${bodyAsText()}")

           assertEquals(HttpStatusCode.OK, status)
           assertTrue(mockRepository.findByIdCalled)
       }
    }

    @Test
    fun `GET term by id returns NotFound when term does not exist`() = testApplication {
        val mockRepository = MockTermRepository(findByIdResult = null)

        setupTestApplication(mockRepository)

        client.get("/terms/999") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `GET term by id returns BadRequest with invalid id`() = testApplication {
        val mockRepository = MockTermRepository()

        setupTestApplication(mockRepository)

        client.get("/terms/invalid") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun `POST creates term and returns Created`() = testApplication {
        val mockRepository = MockTermRepository(
            createResult = TermDTO(id = 1, userId = 1, title = "Term 1", content = "Term 1 Content")
        )

        setupTestApplication(mockRepository)

        client.post("/terms") {
            bearerAuth("test-token")
            contentType(ContentType.Application.Json)
            setBody("""{"title":"New Term","content":"New Term 1 Content","active":true}""")
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertTrue(mockRepository.createCalled)
        }
    }

    @Test
    fun `PUT updates term and returns Accepted`() = testApplication {
        val mockRepository = MockTermRepository()

        setupTestApplication(mockRepository)

        client.put("/terms") {
            bearerAuth("test-token")
            contentType(ContentType.Application.Json)
            setBody("""{"id":1,"title":"New Term","content":"New Term 1 Content","active":true}""")
        }.apply {
            assertEquals(HttpStatusCode.Accepted, status)
            assertTrue(mockRepository.updateCalled)
        }
    }

    @Test
    fun `DELETE removes term and returns NoContent when successful`() = testApplication {
        val mockRepository = MockTermRepository(deleteResult = true)

        setupTestApplication(mockRepository)

        client.delete("/terms/1") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, status)
            assertTrue(mockRepository.deleteCalled)
        }
    }

    @Test
    fun `DELETE returns NotFound when term does not exist`() = testApplication {
        val mockRepository = MockTermRepository(deleteResult = false)

        setupTestApplication(mockRepository)

        client.delete("/terms/999") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `DELETE returns BadRequest with invalid id`() = testApplication {
        val mockRepository = MockTermRepository()

        setupTestApplication(mockRepository)

        client.delete("/terms/invalid") {
            bearerAuth("test-token")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    private class MockTermRepository(
        private val findAllResult: List<TermDTO> = emptyList(),
        private val findByIdResult: TermDTO? = null,
        private val createResult: TermDTO? = null,
        private val deleteResult: Boolean = false
    ) : TermRepositoryInterface {
        var findAllCalled = false
        var findByIdCalled = false
        var createCalled = false
        var updateCalled = false
        var deleteCalled = false

        override fun findAll(userId: Long): List<TermDTO> {
            findAllCalled = true
            return findAllResult
        }

        override fun findById(id: Long, userId: Long): TermDTO? {
            findByIdCalled = true
            return findByIdResult
        }

        override fun create(entity: CreateTermRequest, user_id: Long): TermDTO {
            createCalled = true
            return createResult!!
        }

        override fun update(entity: UpdateTermRequest, userId: Long) {
            updateCalled = true
        }

        override fun delete(id: Long, userId: Long): Boolean {
            deleteCalled = true
            return deleteResult
        }
    }
}