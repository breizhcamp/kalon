package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberKeycloakCRUD (
    private val memberPort: MemberPort
) {

    fun getKeycloakId(id: UUID): UUID? = memberPort.getKeycloakId(id)

}