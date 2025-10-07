package prof.db;

import prof.Requests.CreateTermRequest
import prof.Requests.UpdateTermRequest
import prof.entities.TermDTO;

interface TermRepositoryInterface {
    fun findAll(userId: Long): List<TermDTO>
    fun create(entity: CreateTermRequest, user_id: Long ): TermDTO
    fun update(entity: UpdateTermRequest, userId: Long)
    fun delete(id: Long, userId: Long): Boolean
    fun findById(id: Long, userId: Long): TermDTO?
}
