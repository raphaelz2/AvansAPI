package prof.db

import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.entities.User

interface UserRepository {
    suspend fun findById(id: Long): User?
    suspend fun findByFirstname(firstName: String): User?
    suspend fun findAll(): List<User>
    suspend fun create(entity: CreateUserRequest): User
    suspend fun update(entity: UpdateUserRequest)
    suspend fun delete(id: Long): Boolean
    suspend fun findByEmail(email: String): User?
}
