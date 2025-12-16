package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "event")
class EventDB(

    @Id
    val id: String,

    var name: String,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var website: String?,
    var venue: String?,
)
