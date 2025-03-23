package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.EventParticipant
import org.breizhcamp.kalon.testUtils.generateRandomEventDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventMapperTest {

    @Test
    fun `toEvent should transmit all values and create an empty set for participants`() {
        val eventDB = generateRandomEventDB()

        val event = eventDB.toEvent()
        val emptyParticipantSet: Set<EventParticipant> = emptySet()

        assertEquals(event.id, eventDB.id)
        assertEquals(event.year, eventDB.year)
        assertEquals(event.name, eventDB.name)
        assertEquals(event.debutEvent, eventDB.debutEvent)
        assertEquals(event.finEvent, eventDB.finEvent)
        assertEquals(event.debutCFP, eventDB.debutCFP)
        assertEquals(event.finCFP, eventDB.finCFP)
        assertEquals(event.debutInscription, eventDB.debutInscription)
        assertEquals(event.finInscription, eventDB.finInscription)
        assertEquals(event.website, eventDB.website)
        assertEquals(event.eventParticipants, emptyParticipantSet)
    }
}