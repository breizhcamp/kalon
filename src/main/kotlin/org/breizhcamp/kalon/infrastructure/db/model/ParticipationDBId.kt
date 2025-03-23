package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class ParticipationDBId (
    @Column(name = "member_id")
    val memberId: UUID,

    @Column(name = "team_id")
    val teamId: UUID,

    @Column(name = "event_id")
    val eventId: Int,
): Serializable