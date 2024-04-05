package org.breizhcamp.kalon.infrastructure.db.model

data class MemberParticipationDB(
    val teamDB: TeamDB,
    val eventDB: EventDB,
)
