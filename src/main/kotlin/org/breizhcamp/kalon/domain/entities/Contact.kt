package org.breizhcamp.kalon.domain.entities

import java.util.UUID

data class Contact(
    val id: UUID,
    val platform: String,
    val link: String,
)
