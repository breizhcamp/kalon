package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberExists (
    private val memberPort: MemberPort
) {
    fun exists(id: UUID): Boolean = memberPort.existsById(id)
    fun contactExists(id: UUID, contactId: UUID): Boolean = memberPort.contactExistsById(id, contactId)
}