
package prof

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import prof.db.fake.FakeUserRepository

class GetAllUsersTest {

    @Test
    fun `findAll should return all users in the repository`() = runBlocking {
        // Act: haal alle gebruikers op
        val users = FakeUserRepository.findAll()

        // Assert: controleer dat de lijst niet leeg is
        assertTrue(users.isNotEmpty(), "De lijst met gebruikers mag niet leeg zijn.")

        // (optioneel) controleer minimaal verwachte hoeveelheid seeded gebruikers
        assertTrue(users.size >= 3, "Er zouden minstens 3 standaardgebruikers moeten zijn.")

        // (optioneel) controleer of bepaalde gebruikers in de lijst zitten
        val emails = users.map { it.email }
        assertTrue("admin@avans.nl" in emails, "Admin gebruiker zou aanwezig moeten zijn.")
        assertTrue("anne@avans.nl" in emails, "Anne gebruiker zou aanwezig moeten zijn.")
        assertTrue("henk@avans.nl" in emails, "Henk gebruiker zou aanwezig moeten zijn.")
    }
}
