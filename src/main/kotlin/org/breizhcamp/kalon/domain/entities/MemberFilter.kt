package org.breizhcamp.kalon.domain.entities

import java.util.*

data class MemberFilter(
    val teamId: UUID?,
    val eventId: Int?,
    val nameOrder: Order?,
    val limit: Long?,
) {
    companion object {
        fun empty() = MemberFilter(
            teamId = null,
            eventId = null,
            nameOrder = Order.ASC,
            limit = null)
    }
}
