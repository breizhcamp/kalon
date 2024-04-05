package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberParticipationDB
import java.util.*

interface MemberRepoCustom {

    fun filter(filter: MemberFilter): List<MemberDB>
    fun getParticipations(id: UUID): Set<MemberParticipationDB>

}