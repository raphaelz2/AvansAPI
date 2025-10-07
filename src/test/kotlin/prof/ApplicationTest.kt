package prof

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import prof.entities.Car
import prof.entities.LoginRequest
import prof.entities.Reservation
import prof.enums.PowerSourceTypeEnum
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testCarCRUDOperations() = testApplication {
//        // Sample Car data
//        val car = Car(
//            id = 1,
//            make = "Toyota",
//            model = "Corolla",
//            price = 20000f,
//            pickupLocation = "Amsterdam",
//            category = "Sedan",
//            powerSourceType = PowerSourceTypeEnum.ICE,
//            imageFileNames = mutableListOf("test1.jpg", "test2.jpg"),
//            LocalDateTime(2021, 3, 27, 2, 16, 20),
//            LocalDateTime(2021, 3, 27, 2, 16, 20)
//        )
//
//        // Serialize Car object
//        val carJson = Json.encodeToString(car)
//
//        // 1. Create Car (POST request)
//        val postResponse = client.post("/cars") {
//            contentType(ContentType.Application.Json)
//            setBody(carJson)
//        }
//
//        assertEquals(HttpStatusCode.Created, postResponse.status)
//        val createdCar = Json.decodeFromString<Car>(postResponse.bodyAsText())
//
//        // 2. Retrieve Car (GET request)
//        val getResponse = client.get("/cars/${createdCar.id}")
//        assertEquals(HttpStatusCode.OK, getResponse.status)
//        val retrievedCar = Json.decodeFromString<Car>(getResponse.bodyAsText())
//        assertEquals(createdCar, retrievedCar)
//
//        // 3. Delete Car (DELETE request)
//        val deleteResponse = client.delete("/cars/${createdCar.id}")
//        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)
//
//        // 4. Verify Deletion by Trying to Retrieve Again
//        val getDeletedResponse = client.get("/cars/${createdCar.id}")
//        assertEquals(HttpStatusCode.NotFound, getDeletedResponse.status)
    }

    @Test
    fun testReservationCreation() = testApplication {
//        // Sample Reservation data
//        val reservation = Reservation(
//            id = 1,
//            startTime = LocalDateTime.parse("2024-10-01T10:00:00"),
//            endTime = LocalDateTime.parse("2024-10-01T12:00:00"),
//            userId = 1,
//            carId = 1,
//            LocalDateTime(2021, 3, 27, 2, 16, 20),
//            LocalDateTime(2021, 3, 27, 2, 16, 20)
//        )
//
//        val reservationJson = Json.encodeToString(reservation)
//
//        // 1. Create Reservation
//        val postResponse = client.post("/reservations") {
//            contentType(ContentType.Application.Json)
//            setBody(reservationJson)
//        }
//        assertEquals(HttpStatusCode.Created, postResponse.status)
//
//        // Decode and verify reservation
//        val createdReservation = Json.decodeFromString<Reservation>(postResponse.bodyAsText())
//        assertEquals(reservation.startTime, createdReservation.startTime)
//        assertEquals(reservation.endTime, createdReservation.endTime)
    }

    @Test
    fun testLogin() = testApplication {
//        val loginRequest = LoginRequest(email = "test@example.com", password = "password123")
//        val loginJson = Json.encodeToString(loginRequest)
//
//        val loginResponse = client.post("/login") {
//            contentType(ContentType.Application.Json)
//            setBody(loginJson)
//        }
//
//        assertEquals(HttpStatusCode.OK, loginResponse.status)
    }
}
