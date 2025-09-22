package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import prof.Requests.CreateUserRequest
import prof.Requests.UpdateUserRequest
import prof.db.UserRepository
import prof.entities.User

class SqlUserRepository : UserRepository {

    private fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        firstName = row[Users.firstName],
        lastName = row[Users.lastName],
        password = row[Users.password],
        email = row[Users.email],
        createdAt = LocalDateTime.parse(row[Users.createdAt]),
        modifiedAt = LocalDateTime.parse(row[Users.modifiedAt])
    )

    override suspend fun findById(id: Long): User? = transaction {
        Users.selectAll().where { Users.id eq id }.singleOrNull()?.let { rowToUser(it) }
    }

    override suspend fun findByFirstname(firstName: String): User? = transaction {
        Users.selectAll().where { Users.firstName eq firstName }.limit(1).firstOrNull()?.let { rowToUser(it) }
    }

    override suspend fun findAll(): List<User> = transaction {
        Users.selectAll().map { rowToUser(it) }
    }

    override suspend fun create(entity: CreateUserRequest): User = transaction {
        val newId: Long = Users.insert { st ->
            st[firstName] = entity.firstName
            st[lastName] = entity.lastName
            st[password] = entity.password
            st[email] = entity.email
            st[createdAt] = entity.createdAt.toString()
            st[modifiedAt] = entity.modifiedAt.toString()
        } get Users.id
        Users.selectAll().where { Users.id eq newId }.single().let { rowToUser(it) }
    }

    override suspend fun update(entity: UpdateUserRequest) {
        transaction {
            Users.update({ Users.id eq entity.id }) { st ->
                st[firstName] = entity.firstName
                st[lastName] = entity.lastName
                st[email] = entity.email
                st[createdAt] = entity.createdAt.toString()
                st[modifiedAt] = entity.modifiedAt.toString()
            }
        }
    }

    override suspend fun delete(id: Long): Boolean = transaction {
        Users.deleteWhere { Users.id eq id } > 0
    }

    // Robust email lookup: trims and ignores case to avoid invisible character issues
    override suspend fun findByEmail(email: String): User? = transaction {
        Users
            .selectAll()
            .where { Users.email eq email }
            .limit(1)
            .firstOrNull()
            ?.let { rowToUser(it) }
    }
}
