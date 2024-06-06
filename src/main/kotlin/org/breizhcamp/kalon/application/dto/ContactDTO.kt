package org.breizhcamp.kalon.application.dto

import java.util.*

data class ContactDTO(
    val id: UUID,
    val platform: String,
    val link: String
)
