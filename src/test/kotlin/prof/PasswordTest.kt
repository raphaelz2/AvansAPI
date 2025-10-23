package prof

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import prof.security.Passwords
//test
class PasswordTest {

    @Test
    fun `hash should return a bcrypt hashed password`() {
        val plain = "securePassword123"
        val hashed = Passwords.hash(plain)

        // Controleer dat het gehashte wachtwoord niet gelijk is aan het platte wachtwoord
        assertNotEquals(plain, hashed)

        // Controleer dat het gehashte wachtwoord begint met "$2" (standaard prefix voor bcrypt-hashes)
        assertTrue(hashed.startsWith("\$2"))
    }

    @Test
    fun `verify should return true for correct password`() {
        val plain = "securePassword123"
        val hash = Passwords.hash(plain)

        // Controleer dat het wachtwoord correct wordt geverifieerd
        val result = Passwords.verify(plain, hash)

        // Verwacht dat de verificatie slaagt (true)
        assertTrue(result)
    }

    @Test
    fun `verify should return false for incorrect password`() {
        val correct = "correctPassword"
        val incorrect = "wrongPassword"
        val hash = Passwords.hash(correct)

        // Probeer een fout wachtwoord te verifiëren tegen de hash van het correcte wachtwoord
        val result = Passwords.verify(incorrect, hash)

        // Verwacht dat de verificatie mislukt (false)
        assertFalse(result)
    }
}