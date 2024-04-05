package org.breizhcamp.kalon.domain.entities

import java.time.LocalDateTime

data class EventPartial(
    val name: String?,
    val year: Int?,
    val debutEvent: LocalDateTime?,
    val finEvent: LocalDateTime?,
    val debutCFP: LocalDateTime?,
    val finCFP: LocalDateTime?,
    val debutInscription: LocalDateTime?,
    val finInscription: LocalDateTime?,
    val website: String?,
) {
    companion object{
        fun empty() = EventPartial(null, null, null, null, null, null, null, null, null)
    }
}
