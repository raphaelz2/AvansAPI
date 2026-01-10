package prof

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import prof.Requests.CreateUserRequest
import prof.db.fake.FakeUserRepository

class DisableUserTest {

    @Test
    fun `setDisabled should disable existing user and return true`() = runBlocking {
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

        // Act: disable de gebruiker
        val ok = FakeUserRepository.setDisabled(userId, 1)

        // Assert: verwijdering is gelukt
        assertTrue(ok, "Verwacht dat de gebruiker succesvol disabled is.")

        // Assert: gebruiker bestaat niet meer
        val retrieved = FakeUserRepository.findById(userId)
        assertNotNull(retrieved, "Verwacht dat de gebruiker nog bestaat na soft delete.")
        assertEquals(1, retrieved!!.disabled, "Verwacht dat de gebruiker disabled=1 is.")
    }

    @Test
    fun `setDisabled should return false if user does not exist`() = runBlocking {
        // Arrange: gebruik een ID die niet bestaat
        val nonExistentId = -999L

        // Act
        val ok = FakeUserRepository.setDisabled(nonExistentId, 1)

        // Assert
        assertFalse(ok, "Verwacht dat disable van een niet-bestaande gebruiker false retourneert.")
    }
}