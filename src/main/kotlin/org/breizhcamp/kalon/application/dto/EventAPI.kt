package org.breizhcamp.kalon.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.breizhcamp.kalon.config.log.KalonMDC
import org.breizhcamp.kalon.config.log.LogMDC
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import java.time.LocalDate

@Schema(description = "Event information")
sealed class EventAPI {
    abstract val id: String
    abstract val name: String
}

@Schema(description = "Complete event information")
data class EventFullAPI(
    @field:Schema(description = "Unique identifier", example = "myevent-2026")
    override val id: String,

    @field:Schema(description = "Title of the event", example = "My Event 2026")
    override val name: String,

    @field:Schema(description = "Event start date", example = "2026-06-15")
    val startDate: LocalDate,

    @field:Schema(description = "Event end date", example = "2026-06-17")
    val endDate: LocalDate,

    @field:Schema(description = "Event website URL", example = "https://www.myevent.org")
    val website: String?,

    @field:Schema(description = "Event venue", example = "Conference Center, City")
    val venue: String?,
) : EventAPI(), LogMDC {
    fun toDomain(id: String? = null) = Event(
        id = EventId(id ?: this.id),
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

@Schema(description = "Summary event information with id and name only")
data class EventSummaryAPI(
    @field:Schema(description = "Unique identifier", example = "myevent-2026")
    override val id: String,

    @field:Schema(description = "Title of the event", example = "My Event 2026")
    override val name: String,
) : EventAPI()

fun Event.toFullApi() = EventFullAPI(
    id = this.id.value,
    name = this.name,
    startDate = this.startDate,
    endDate = this.endDate,
    website = this.website,
    venue = this.venue,
)

fun Event.toSummaryApi() = EventSummaryAPI(
    id = this.id.value,
    name = this.name,
)

