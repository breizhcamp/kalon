package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberDeleteContact(
    private val memberPort: MemberPort
) {
    fun delete(id: UUID, contactId: UUID): Member = memberPort.removeContact(id, contactId)
}