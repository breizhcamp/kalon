package org.breizhcamp.kalon.domain.entities

import java.util.*

data class EventFilter(
    val yearBefore: Int?,
    val yearAfter: Int?,
    val hasParticipantMemberId: UUID?,
    val hasParticipatingTeamId: UUID?,
    val yearOrder: Order?,
    val limit: Long?
) {

    companion object {
        fun default() = EventFilter(
            yearBefore = null,
            yearAfter = null,
            hasParticipantMemberId = null,
            hasParticipatingTeamId = null,
            yearOrder = Order.ASC,
            limit = null
        )
    }

}
