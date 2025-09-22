package prof.db

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.entities.User

object FakeUserRepository : UserRepository {
    private var currentId: Long = 0L
    private val users = mutableListOf<User>()
    // Seed the fake UserRepository with some dummy data
    init {
        runBlocking {
            create(
                CreateUserRequest(
                    "Admin",
                    "",
                    "Admin1",
                    "admin@avans.nl",
                    LocalDateTime(2021, 3, 27, 2, 16, 20),
                    LocalDateTime(2021, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateUserRequest(
                    "Anne",
                    "Jong",
                    "Anne1",
                    "anne@avans.nl",
                    LocalDateTime(2021, 3, 27, 2, 16, 20),
                    LocalDateTime(2021, 3, 27, 2, 16, 20)
                )
            )
            create(
                CreateUserRequest(
                    "Henk",
                    "Koster",
                    "Henk1",
                    "henk@avans.nl",
                    LocalDateTime(2021, 3, 27, 2, 16, 20),
                    LocalDateTime(2021, 3, 27, 2, 16, 20)
                )
            )
        }
    }

    // Find a user by their email
    override suspend fun findByEmail(email: String): User? = users.find { it.email == email }

    // Find a user by their first name
    override suspend fun findByFirstname(firstName: String): User? = users.find { it.firstName == firstName }

    // Find all users
    override suspend fun findAll(): List<User> = users.toList()

    // Find a user by their ID
    override suspend fun findById(id: Long): User? = users.find { it.id == id }

// Create a new user
    override suspend fun create(entity: CreateUserRequest): User {
        currentId++ // Increment the ID for the new user
        val now = Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Amsterdam")) // Get current time in Amsterdam

        // Create a new User instance, assigning values from the request entity to each property
        val newUser = User(
            id = currentId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            password = entity.password,
            email = entity.email,
            createdAt = now,
            modifiedAt = now
        )

        users.add(newUser) // Add the new user to the list
        return newUser // Return the newly created user
    }

    // Update an existing user
    override suspend fun update(entity: UpdateUserRequest) {
        val user = users.find { it.id == entity.id } ?: throw IllegalArgumentException("User with ID ${entity.id} does not exist")

        // Update the properties of the found user to match those in the update request
        user.apply {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
            modifiedAt = entity.modifiedAt
            createdAt = entity.createdAt
        }
    }

    // Delete a user by their ID
    override suspend fun delete(id: Long): Boolean = users.removeIf { it.id == id }
}
