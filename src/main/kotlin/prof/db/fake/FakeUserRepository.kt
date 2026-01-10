package prof.db.fake

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.db.UserRepositoryInterface
import prof.entities.UserDTO
import prof.security.Passwords

object FakeUserRepository : UserRepositoryInterface {
    private var currentId: Long = 0L
    private val users = mutableListOf<UserDTO>()
    init {
        runBlocking {
            create(
                CreateUserRequest(
                    "Admin",
                    "",
                    "Admin1",
                    "admin@avans.nl",
                )
            )
            create(
                CreateUserRequest(
                    "Anne",
                    "Jong",
                    "Anne1",
                    "anne@avans.nl",
                )
            )
            create(
                CreateUserRequest(
                    "Henk",
                    "Koster",
                    "Henk1",
                    "henk@avans.nl",
                )
            )
        }
    }

    // Find a user by their email
    override suspend fun findByEmail(email: String): UserDTO? = users.find { it.email == email }

    // Find a user by their first name
    override suspend fun findByFirstname(firstName: String): UserDTO? = users.find { it.firstName == firstName }

    // Find all users
    override suspend fun findAll(): List<UserDTO> = users.toList()

    // Find a user by their ID
    override suspend fun findById(id: Long): UserDTO? = users.find { it.id == id }

// Create a new user
    override suspend fun create(entity: CreateUserRequest): UserDTO {
        val hashed = Passwords.hash(entity.password)
        currentId++ // Increment the ID for the new user
        val now = Clock.System.now().toLocalDateTime(TimeZone.Companion.of("Europe/Amsterdam")) // Get current time in Amsterdam

        // Create a new prof.security.User instance, assigning values from the request entity to each property
        val newUser = UserDTO(
            id = currentId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            password = hashed,
            email = entity.email,
            disabled = 0,
            createdAt = now,
            modifiedAt = now
        )

    println("Gehashed wachtwoord opgeslagen: ${newUser.password}")
        users.add(newUser) // Add the new user to the list
        return newUser // Return the newly created user
    }


    // Update an existing user
    override suspend fun update(entity: UpdateUserRequest) {
        val user = users.find { it.id == entity.id } ?: throw IllegalArgumentException("prof.security.User with ID ${entity.id} does not exist")

        // Update the properties of the found user to match those in the update request
        user.apply {
            firstName = entity.firstName
            lastName = entity.lastName
            email = entity.email
        }
    }

    override suspend fun setDisabled(id: Long, disabled: Int): Boolean {
        val user = users.find { it.id == id } ?: return false
        user.disabled = disabled
        return true
    }
}