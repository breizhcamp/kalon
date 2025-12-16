package org.breizhcamp.kalon.helpers

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import java.time.LocalDate

object EventHelper {

    fun get(
        id: String = "breizhcamp-2025",
        name: String = "BreizhCamp 2025",
        startDate: LocalDate = LocalDate.of(2025, 6, 25),
        endDate: LocalDate = LocalDate.of(2025, 6, 27),
        website: String? = "https://www.breizhcamp.org/",
        venue: String? = "Université de Rennes, Bâtiment 2A, 263, avenue du Général Leclerc, 35042 Rennes",
    ) = Event(
        id = EventId(id),
        name = name,
        startDate = startDate,
        endDate = endDate,
        website = website,
        venue = venue,
    )

}
