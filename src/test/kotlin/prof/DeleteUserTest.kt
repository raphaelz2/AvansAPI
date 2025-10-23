package prof

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import prof.Requests.CreateUserRequest
import prof.db.fake.FakeUserRepository

class DeleteUserTest {

    @Test
    fun `delete should remove existing user and return true`() = runBlocking {
        // Arrange: maak een nieuwe gebruiker aan
        val user = FakeUserRepository.create(
            CreateUserRequest(
                firstName = "Delete",
                lastName = "User",
                password = "delete123",
                email = "delete.user@example.com",
            )
        )

        val userId = user.id!!

        // Act: verwijder de gebruiker
        val deleted = FakeUserRepository.delete(userId)

        // Assert: verwijdering is gelukt
        assertTrue(deleted, "Verwacht dat de gebruiker succesvol verwijderd is.")

        // Assert: gebruiker bestaat niet meer
        val retrieved = FakeUserRepository.findById(userId)
        assertNull(retrieved, "Verwacht dat de gebruiker niet meer gevonden wordt na verwijderen.")
    }

    @Test
    fun `delete should return false if user does not exist`() = runBlocking {
        // Arrange: gebruik een ID die niet bestaat
        val nonExistentId = -999L

        // Act
        val deleted = FakeUserRepository.delete(nonExistentId)

        // Assert
        assertFalse(deleted, "Verwacht dat verwijderen van een niet-bestaande gebruiker false retourneert.")
    }
}