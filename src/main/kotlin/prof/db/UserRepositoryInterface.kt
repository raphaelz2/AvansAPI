package prof.db

import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.entities.UserDTO

interface UserRepositoryInterface {
    suspend fun findById(id: Long): UserDTO?
    suspend fun findByFirstname(firstName: String): UserDTO?
    suspend fun findAll(): List<UserDTO>
    suspend fun create(entity: CreateUserRequest): UserDTO
    suspend fun update(entity: UpdateUserRequest)
    suspend fun delete(id: Long): Boolean
    suspend fun findByEmail(email: String): UserDTO?
}
