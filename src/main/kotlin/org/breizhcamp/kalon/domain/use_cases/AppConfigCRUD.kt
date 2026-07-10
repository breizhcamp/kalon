package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.config.annotations.Tx
import org.breizhcamp.kalon.config.annotations.UseCase
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.exceptions.NotFoundException
import org.breizhcamp.kalon.domain.ports.AppConfigPort
import org.breizhcamp.kalon.domain.ports.EventPort

@UseCase
class AppConfigCRUD(
    private val eventPort: EventPort,
    private val appConfigPort: AppConfigPort,
) {

    fun getDefaultEvent(): Event =
        eventPort.getDefaultEvent()
            ?: throw NotFoundException("Default event not found")

    @Tx
    fun setDefaultEvent(eventId: EventId) {
        if (!eventPort.isIdExists(eventId)) {
            throw NotFoundException<Event>(eventId)
        }
        appConfigPort.setDefaultEvent(eventId)
    }
}
