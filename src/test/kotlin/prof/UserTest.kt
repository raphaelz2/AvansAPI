package prof
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import prof.Requests.CreateUserRequest
import prof.db.fake.FakeUserRepository
import prof.security.Passwords


class UserTest {
    @Test
    fun `create user stores hashed password and is verifiable`() = runBlocking {
        val req = CreateUserRequest(
            firstName = "Admin",
            lastName = "User",
            password = "secret",
            email = "admin@example.com",
        )

        val user = FakeUserRepository.create(req)

        // password must not be stored as plaintext
        assertNotEquals("secret", user.password)

        // and must verify against the original plain-text
        assertTrue(Passwords.verify("secret", user.password))
    }
}
