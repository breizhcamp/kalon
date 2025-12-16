package org.breizhcamp.kalon.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.breizhcamp.kalon.config.log.KalonMDC
import org.breizhcamp.kalon.config.log.LogMDC
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import java.time.LocalDate

data class EventAPI(
    @field:Schema(description = "Unique identifier", example = "myevent-2026")
    val id: String,

    @field:Schema(description = "Title of the event", example = "My Event 2026")
    val name: String,

    @field:Schema(description = "Event start date", example = "2026-06-15")
    val startDate: LocalDate,

    @field:Schema(description = "Event end date", example = "2026-06-17")
    val endDate: LocalDate,

    @field:Schema(description = "Event website URL", example = "https://www.myevent.org")
    val website: String?,

    @field:Schema(description = "Event venue", example = "Conference Center, City")
    val venue: String?,
): LogMDC {
    fun toDomain() = Event(
        id = EventId(id),
        name = name,
        startDate = startDate,
        endDate = endDate,
        website = website,
        venue = venue,
    )

    override fun mdc(): Map<String, String> = mapOf(
        KalonMDC.EVENT_ID to id,
    )
}

fun Event.toApi() = EventAPI(
    id = this.id.value,
    name = this.name,
    startDate = this.startDate,
    endDate = this.endDate,
    website = this.website,
    venue = this.venue,
)

