package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "app_config")
class AppConfigDB(
    @Id
    val id: Int = 1,
    var defaultEventId: String? = null,
)
