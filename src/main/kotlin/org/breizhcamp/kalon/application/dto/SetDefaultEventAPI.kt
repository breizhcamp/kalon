package org.breizhcamp.kalon.application.dto

import jakarta.validation.constraints.NotBlank
import org.breizhcamp.kalon.domain.entities.EventId

data class SetDefaultEventAPI(
    @field:NotBlank
    val defaultEventId: EventId,
)
