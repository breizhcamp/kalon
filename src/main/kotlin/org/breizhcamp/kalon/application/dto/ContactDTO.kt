package org.breizhcamp.kalon.application.dto

import java.util.UUID

data class ContactDTO(
    val id: UUID,
    val platform: String,
    val link: String
)
