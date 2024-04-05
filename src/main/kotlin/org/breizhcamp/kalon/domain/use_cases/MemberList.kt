package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service

@Service
class MemberList (
    private val memberPort: MemberPort
) {
    fun list(filter: MemberFilter): List<Member> = memberPort.list(filter)
}