package org.breizhcamp.kalon.infrastructure.db.model

data class TeamParticipationDB(
    val memberDB: MemberDB,
    val eventDB: EventDB,
)
