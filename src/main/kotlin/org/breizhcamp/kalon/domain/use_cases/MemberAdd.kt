package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service

@Service
@Transactional
class MemberAdd (
    private val memberPort: MemberPort
) {
    fun add(creationReq: MemberCreationReq): Member = memberPort.add(creationReq)
}