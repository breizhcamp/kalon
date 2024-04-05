package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberGet (
    private val memberPort: MemberPort
) {
    fun getById(id: UUID) = memberPort.getById(id)
}