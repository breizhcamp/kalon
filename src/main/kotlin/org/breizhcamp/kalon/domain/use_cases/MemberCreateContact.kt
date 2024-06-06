package org.breizhcamp.kalon.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class MemberCreateContact(
    private val memberPort: MemberPort
) {
    fun create(id: UUID, platform: String, link: String): Member = memberPort.addContact(id, platform, link)
}