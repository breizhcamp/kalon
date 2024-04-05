package org.breizhcamp.kalon.domain.entities

import java.util.*

data class TeamFilter (
    val memberId: UUID?,
    val eventId: Int?,
) {
    companion object {
        fun empty() = TeamFilter(null, null)
    }
}