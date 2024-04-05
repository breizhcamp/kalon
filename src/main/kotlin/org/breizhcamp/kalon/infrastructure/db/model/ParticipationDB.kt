package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "participation")
data class ParticipationDB(
    @EmbeddedId
    val participationDBId: ParticipationDBId
)
