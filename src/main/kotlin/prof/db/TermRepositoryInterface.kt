package prof.db;

import prof.Requests.UpdateTermRequest
import prof.entities.TermDTO;

interface TermRepositoryInterface {
    fun findAll(): List<TermDTO>
    fun update(entity: UpdateTermRequest)
    fun delete(id: Long): Boolean
    fun findById(id: Long): TermDTO?
}
