package org.breizhcamp.kalon.domain.use_cases.ports

import java.util.*

interface ParticipationPort {

    fun existsByIds(teamId: UUID, memberId: UUID, eventId: Int): Boolean
    fun createByIds(teamId: UUID, memberId: UUID, eventId: Int)
    fun removeByIds(teamId: UUID, memberId: UUID, eventId: Int)

}