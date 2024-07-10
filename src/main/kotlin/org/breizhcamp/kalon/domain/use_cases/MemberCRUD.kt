package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.application.requests.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberCRUD(
    private val memberPort: MemberPort
) {
    fun add(creationReq: MemberCreationReq): Member = memberPort.add(creationReq)
    fun list(filter: MemberFilter): List<Member> = memberPort.list(filter)
    fun getById(id: UUID) = memberPort.getById(id)
    fun update(id: UUID, member: MemberPartial): Member = memberPort.update(id, member)
}