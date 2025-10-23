package prof

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.*
import prof.routes.reservationRoutes
import prof.db.ReservationRepositoryInterface
import prof.Requests.CreateReservationRequest
import prof.entities.ReservationDTO
import kotlinx.datetime.LocalDateTime
import prof.Requests.UpdateReservationRequest
import prof.enums.ReservationStatusEnum
import java.math.BigDecimal

class ReservationTest {

    private fun ApplicationTestBuilder.setupTestApplication(repository: ReservationRepositoryInterface) {
        application {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            routing {
                reservationRoutes(repository)
            }
        }
    }

    @Test
    fun `GET all reservations returns OK with reservation list`() = testApplication {
        val mockRepository = MockReservationRepository(
            findAllResult = listOf(
                ReservationDTO(
                    id = 1,
                    startTime = LocalDateTime(2025, 10, 15, 8, 0),
                    endTime = LocalDateTime(2025, 10, 15, 12, 0),
                    userId = 1,
                    carId = 10,
                    termId = 1,
                    status = ReservationStatusEnum.CONFIRMED,
                    startMileage = 12000,
                    endMileage = 12100,
                    costPerKm = BigDecimal("0.25"),
                    createdAt = LocalDateTime(2025, 10, 15, 8, 0),
                    modifiedAt = LocalDateTime(2025, 10, 15, 12, 0)
                ),
                ReservationDTO(
                    id = 2,
                    startTime = LocalDateTime(2025, 10, 16, 9, 0),
                    endTime = LocalDateTime(2025, 10, 16, 13, 0),
                    userId = 2,
                    carId = 11,
                    termId = 1,
                    status = ReservationStatusEnum.PENDING,
                    startMileage = 5400,
                    endMileage = 5500,
                    costPerKm = BigDecimal("0.30"),
                    createdAt = LocalDateTime(2025, 10, 16, 13, 0),
                    modifiedAt = LocalDateTime(2025, 10, 16, 13, 0)
                )
            )
        )

        setupTestApplication(mockRepository)

        client.get("/reservations").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(mockRepository.findAllCalled)
        }
    }

    @Test
    fun `GET reservation by id returns OK when reservation exists`() = testApplication {
        val mockRepository = MockReservationRepository(
            findByIdResult =  ReservationDTO(
                id = 2,
                startTime = LocalDateTime(2025, 10, 16, 9, 0),
                endTime = LocalDateTime(2025, 10, 16, 13, 0),
                userId = 2,
                carId = 11,
                termId = 1,
                status = ReservationStatusEnum.PENDING,
                startMileage = 5400,
                endMileage = 5500,
                costPerKm = BigDecimal("0.30"),
                createdAt = LocalDateTime(2025, 10, 16, 13, 0),
                modifiedAt = LocalDateTime(2025, 10, 16, 13, 0)
            )
        )

        setupTestApplication(mockRepository)

        client.get("/reservations/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(mockRepository.findByIdCalled)
        }
    }

    @Test
    fun `GET reservation by id returns NotFound when reservation does not exist`() = testApplication {
        val mockRepository = MockReservationRepository(findByIdResult = null)

        setupTestApplication(mockRepository)

        client.get("/reservations/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `GET reservation by id returns BadRequest with invalid id`() = testApplication {
        val mockRepository = MockReservationRepository()

        setupTestApplication(mockRepository)

        client.get("/reservations/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun `POST creates reservation and returns Created`() = testApplication {
        val mockRepository = MockReservationRepository(
            createResult =  ReservationDTO(
                id = 2,
                startTime = LocalDateTime(2025, 10, 16, 9, 0),
                endTime = LocalDateTime(2025, 10, 16, 13, 0),
                userId = 2,
                carId = 11,
                termId = 1,
                status = ReservationStatusEnum.PENDING,
                startMileage = 5400,
                endMileage = 5500,
                costPerKm = BigDecimal("0.30"),
                createdAt = LocalDateTime(2025, 10, 16, 13, 0),
                modifiedAt = LocalDateTime(2025, 10, 16, 13, 0)
            )
        )

        setupTestApplication(mockRepository)

        client.post("/reservations") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "startTime": "2025-10-15T08:00:00",
                    "endTime": "2025-10-15T12:00:00",
                    "userId": 1,
                     "carId": 1,
                     "termId": 1,
                    "status": "PENDING",
                    "startMileage": 15230,
                    "endMileage": 15300,
                    "costPerKm": "0.25",
                    "createdAt": "2025-10-09T10:00:00",
                     "modifiedAt": "2025-10-09T10:00:00"
                }
                """
            )
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertTrue(mockRepository.createCalled)
        }
    }

    @Test
    fun `DELETE removes reservation and returns NoContent when successful`() = testApplication {
        val mockRepository = MockReservationRepository(deleteResult = true)

        setupTestApplication(mockRepository)

        client.delete("/reservations/1").apply {
            assertEquals(HttpStatusCode.NoContent, status)
            assertTrue(mockRepository.deleteCalled)
        }
    }

    @Test
    fun `DELETE returns NotFound when reservation does not exist`() = testApplication {
        val mockRepository = MockReservationRepository(deleteResult = false)

        setupTestApplication(mockRepository)

        client.delete("/reservations/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun `DELETE returns BadRequest with invalid id`() = testApplication {
        val mockRepository = MockReservationRepository()

        setupTestApplication(mockRepository)

        client.delete("/reservations/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    private class MockReservationRepository(
        private val findAllResult: List<ReservationDTO> = emptyList(),
        private val findByIdResult: ReservationDTO? = null,
        private val createResult: ReservationDTO? = null,
        private val deleteResult: Boolean = false,
        private val updateResult: Unit = Unit,
        private val findReservationsForUserResult: List<ReservationDTO> = emptyList(),
        private val findReservationsForCarResult: List<ReservationDTO> = emptyList(),
        private val findReservationsForCarAndTimeframeResult: List<ReservationDTO> = emptyList()
    ) : ReservationRepositoryInterface {
        var findAllCalled = false
        var findByIdCalled = false
        var createCalled = false
        var deleteCalled = false
        var updateCalled = false
        var findReservationsForUserCalled = false
        var findReservationsForCarCalled = false
        var findReservationsForCarAndTimeframeCalled = false


        override suspend fun findAll(): List<ReservationDTO> {
            findAllCalled = true
            return findAllResult
        }

        override suspend fun canBookOnTime(entity: CreateReservationRequest): Boolean {
            TODO("Not yet implemented")
        }

        override suspend fun findById(id: Long): ReservationDTO? {
            findByIdCalled = true
            return findByIdResult
        }

        override suspend fun create(entity: CreateReservationRequest): ReservationDTO {
            createCalled = true
            return createResult!!
        }

        override suspend fun update(entity: UpdateReservationRequest) {
            updateCalled = true
            return updateResult
        }

        override suspend fun delete(id: Long): Boolean {
            deleteCalled = true
            return deleteResult
        }

        override suspend fun findReservationsForUser(userId: Long): List<ReservationDTO> {
            findReservationsForUserCalled = true
            return findReservationsForUserResult
        }

        override suspend fun findReservationsForCar(carId: Long): List<ReservationDTO> {
            findReservationsForCarCalled = true
            return findReservationsForCarResult
        }

        override suspend fun findReservationsForCarAndTimeframe(
            carId: Long,
            startTime: LocalDateTime,
            endTime: LocalDateTime
        ): List<ReservationDTO> {
            findReservationsForCarAndTimeframeCalled = true
            return findReservationsForCarAndTimeframeResult
        }
    }
}