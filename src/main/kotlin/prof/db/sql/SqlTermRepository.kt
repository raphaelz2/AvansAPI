package prof.db.sql

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import prof.Requests.UpdateTermRequest
import prof.db.TermRepositoryInterface
import prof.db.sql.Terms.active
import prof.db.sql.Terms.content
import prof.db.sql.Terms.title
import prof.db.sql.Users.createdAt
import prof.db.sql.Users.email
import prof.db.sql.Users.firstName
import prof.db.sql.Users.lastName
import prof.db.sql.Users.modifiedAt
import prof.entities.TermDTO
import prof.entities.User
import java.time.Instant

class SqlTermRepository : TermRepositoryInterface {
    private fun rowToTerm(row: ResultRow) = TermDTO(
        id = row[Terms.id],
        title = row[Terms.title],
        content = row[Terms.content],
        version = row[Terms.version],
        active = row[Terms.active],
        userId = row[Terms.userId],
        createdAt = LocalDateTime.parse(row[Terms.createdAt]),
        modifiedAt = LocalDateTime.parse(row[Terms.modifiedAt])
    )

    override fun findAll(): List<TermDTO> = transaction {
        Terms.selectAll().map { rowToTerm(it) }
    }

//    override fun create(entity: CreateTermRequest): TermDTO = transaction {
//
//        val newId: Long = Terms.insert { st ->
//            st[title] = entity.title
//            st[content] = entity.content
//            st[active] = entity.active
//            st[userId] =
//            st[createdAt] = entity.createdAt.toString()
//            st[modifiedAt] = entity.modifiedAt.toString()
//        } get Terms.id
//        Terms.selectAll().where { Terms.id eq newId }.single().let { rowToTerm(it) }
//    }

    override fun update(entity: UpdateTermRequest) {
        transaction {
            Terms.update({ Terms.id eq entity.id }) { st ->
                st[title] = entity.title
                st[content] = entity.content
                st[active] = entity.active
                st[modifiedAt] = Instant.now().toString()
            }
        }
    }

    override fun delete(id: Long): Boolean = transaction {
        Users.deleteWhere { Terms.id eq id } > 0
    }

    override fun findById(id: Long): TermDTO? = transaction {
        Terms.selectAll().where { Terms.id eq id }.singleOrNull()?.let { rowToTerm(it) }
    }
}