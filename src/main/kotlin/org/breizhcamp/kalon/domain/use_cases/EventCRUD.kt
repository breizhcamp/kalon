package org.breizhcamp.kalon.domain.use_cases

import io.github.oshai.kotlinlogging.KotlinLogging
import org.breizhcamp.kalon.config.annotations.Tx
import org.breizhcamp.kalon.config.annotations.UseCase
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.exceptions.EventIdAlreadyExistsException
import org.breizhcamp.kalon.domain.exceptions.InconsistentStartEndDateException
import org.breizhcamp.kalon.domain.ports.EventPort

private val logger = KotlinLogging.logger {}

@UseCase
class EventCRUD(
    private val eventPort: EventPort,
) {

    @Tx
    fun create(event: Event) {
        logger.info { "Creating event" }
        if (eventPort.isIdExists(event.id)) {
            throw EventIdAlreadyExistsException(event.id)
        }

        if (event.startDate.isAfter(event.endDate)) {
            throw InconsistentStartEndDateException(event.startDate, event.endDate)
        }

        eventPort.create(event)
        logger.info { "Event created" }
    }

}
