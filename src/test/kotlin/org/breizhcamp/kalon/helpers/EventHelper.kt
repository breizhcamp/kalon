package org.breizhcamp.kalon.helpers

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import java.time.LocalDate

object EventHelper {

    fun get(
        startDate: LocalDate = LocalDate.of(2025, 6, 25),
        endDate: LocalDate = LocalDate.of(2025, 6, 27)
    ) = Event(
        id = EventId("breizhcamp-2025"),
        name = "BreizhCamp 2025",
        startDate = startDate,
        endDate = endDate,
        website = "https://www.breizhcamp.org/",
        venue = "Université de Rennes, Bâtiment 2A, 263, avenue du Général Leclerc, 35042 Rennes"
    )

}
