package org.breizhcamp.kalon.domain.use_cases

import io.github.oshai.kotlinlogging.KotlinLogging
import org.breizhcamp.kalon.config.annotations.Tx
import org.breizhcamp.kalon.config.annotations.UseCase
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.exceptions.EventIdAlreadyExistsException
import org.breizhcamp.kalon.domain.exceptions.InconsistentStartEndDateException
import org.breizhcamp.kalon.domain.exceptions.NotFoundException
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

        checkStartBeforeEndDate(event)

        eventPort.create(event)
        logger.info { "Event created" }
    }

    @Tx
    fun update(event: Event) {
        logger.info { "Updating event" }
        if (!eventPort.isIdExists(event.id)) {
            throw NotFoundException<Event>(event.id)
        }

        checkStartBeforeEndDate(event)
        eventPort.update(event)
        logger.info { "Event updated" }
    }

    @Tx
    fun delete(eventId: EventId) {
        logger.info { "Deleting event" }
        if (!eventPort.isIdExists(eventId)) {
            throw NotFoundException<Event>(eventId)
        }
        eventPort.delete(eventId)
        logger.info { "Event deleted" }
    }

    fun list(): List<Event> = eventPort.list()

    private fun checkStartBeforeEndDate(event: Event) {
        if (event.startDate.isAfter(event.endDate)) {
            throw InconsistentStartEndDateException(event.startDate, event.endDate)
        }
    }
}
