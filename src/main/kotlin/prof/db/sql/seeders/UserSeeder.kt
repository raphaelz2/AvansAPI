package prof.db.sql.seeders

import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.selectAll
import prof.Requests.CreateUserRequest
import prof.db.sql.SqlUserRepository
import prof.db.sql.Users

class UserSeeder(
    private val userRepository: SqlUserRepository = SqlUserRepository()
) {
    suspend fun run() {
        if (Users.selectAll().empty()) {
            println("🌱 UserSeeder gestart...")

            val now = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.UTC)

            val users = listOf(
                CreateUserRequest(
                    firstName = "Admin",
                    lastName = "User",
                    password = "admin123",
                    email = "admin@example.com",
                    createdAt = now,
                    modifiedAt = now
                ),
                CreateUserRequest(
                    firstName = "Test",
                    lastName = "Gebruiker",
                    password = "test123",
                    email = "test@example.com",
                    createdAt = now,
                    modifiedAt = now
                )
            )

            users.forEach {
                try {
                    val created = userRepository.create(it)
                    println("✅ Gebruiker toegevoegd: ${created.email}")
                } catch (e: Exception) {
                    println("⚠️ Kon gebruiker ${it.email} niet toevoegen: ${e.message}")
                }
            }

            println("✅ UserSeeder klaar.")
        }
    }
}