package org.breizhcamp.kalon.infrastructure.db.model

data class EventParticipantDB(
    val memberDB: MemberDB,
    val teamDB: TeamDB,
)
