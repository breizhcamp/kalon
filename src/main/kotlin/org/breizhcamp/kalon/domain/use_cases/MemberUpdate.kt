package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberUpdate (
    private val memberPort: MemberPort
) {
    fun update(id: UUID, member: MemberPartial): Member = memberPort.update(id, member)
}