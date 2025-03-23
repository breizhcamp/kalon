package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity @Table(name = "team")
data class TeamDB (
    @Id
    val id: UUID,
    val name: String,
    val description: String?,
)