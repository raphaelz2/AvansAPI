package prof.db.sql

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import prof.Requests.CreateTermRequest
import prof.Requests.UpdateTermRequest
import prof.db.TermRepositoryInterface
import prof.entities.TermDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.max

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

    override fun findAll(userId: Long): List<TermDTO> = transaction {
        Terms
            .selectAll().where { Terms.userId eq userId }
            .map { rowToTerm(it) }
    }

    private fun getNextVersionForUser(tx: Transaction, userId: Long): Int {
        val currentMax = Terms
            .select(Terms.version.max())
            .where { Terms.userId eq userId }
            .singleOrNull()
            ?.getOrNull(Terms.version.max()) ?: 0
        return currentMax + 1
    }

    private fun setAllTermsToStatusFalse(tx: Transaction, userId: Long) {
        Terms.update({ Terms.userId eq userId and (Terms.active eq true) }) { st ->
            st[active] = false
        }
    }

    override fun create(entity: CreateTermRequest, user_id: Long ): TermDTO = transaction {
        val nowUtc = Clock.System.now()
        val nowLocal: LocalDateTime = nowUtc.toLocalDateTime(TimeZone.currentSystemDefault())
        val nextVersion = getNextVersionForUser(this, user_id)
        if(entity.active)
        {
            setAllTermsToStatusFalse(this, user_id)
        }

        val newId: Long = Terms.insert { st ->
            st[title] = entity.title
            st[content] = entity.content
            st[active] = entity.active
            st[userId] = user_id
            st[version] = nextVersion
            st[createdAt] = nowLocal.toString()
            st[modifiedAt] = nowLocal.toString()
        } get Terms.id
        Terms.selectAll().where { Terms.id eq newId }.single().let { rowToTerm(it) }
    }

    override fun update(entity: UpdateTermRequest, userId: Long) {
        transaction {
            val oldTerm = Terms
                .selectAll()
                .where { (Terms.id eq entity.id) and (Terms.userId eq userId) }
                .singleOrNull()
                ?: throw IllegalArgumentException("Term met id ${entity.id} niet gevonden voor gebruiker $userId")

            val oldTitle = oldTerm[Terms.title]
            val oldContent = oldTerm[Terms.content]
            val oldActive = oldTerm[Terms.active]

            val titleChanged = entity.title != oldTitle
            val contentChanged = entity.content != oldContent
            val activeChanged = entity.active != oldActive

            if (!titleChanged && !contentChanged && activeChanged) {
                Terms.update(
                    where = { (Terms.id eq entity.id) and (Terms.userId eq userId) }
                ) { st ->
                    st[active] = entity.active
                    st[modifiedAt] = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .toString()
                }

                if (entity.active) {
                    setAllTermsToStatusFalse(this, userId)
                    Terms.update({ Terms.id eq entity.id }) { st ->
                        st[active] = true
                    }
                }

            } else {
                val newTerm = CreateTermRequest(
                    title = entity.title,
                    content = entity.content,
                    active = entity.active
                )
                create(newTerm, userId)
            }
        }
    }

    override fun delete(id: Long, userId: Long): Boolean = transaction {
        Users.deleteWhere {
            (Terms.id eq id) and (Terms.userId eq userId)
        } > 0
    }

    override fun findById(id: Long, userId: Long): TermDTO? = transaction {
        Terms
            .selectAll().where { (Terms.id eq id) and (Terms.userId eq userId) }
            .singleOrNull()
            ?.let { rowToTerm(it) }
    }
}