package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.infrastructure.db.mappers.toEvent
import org.breizhcamp.kalon.infrastructure.db.repos.EventRepo
import org.breizhcamp.kalon.testUtils.generateRandomEventDB
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAdapter::class)
class EventAdapterTest {

    @MockkBean
    private lateinit var eventRepo: EventRepo

    @Autowired
    private lateinit var eventAdapter: EventAdapter

    @Test
    fun `list should call repo with provided filter and return a list of Events`() {
        val filter = EventFilter.default()
        val returned = listOf(
            generateRandomEventDB(),
            generateRandomEventDB(),
            generateRandomEventDB(),
            generateRandomEventDB()
        )

        every { eventRepo.filter(filter) } returns returned

        assertEquals(eventAdapter.list(filter), returned.map { it.toEvent() })

        verify { eventRepo.filter(filter) }
    }

    @Test
    fun `add should call repo with values in request and return the Event`() {

        val event = generateRandomEventDB()
        val request = EventCreationReq(event.year, event.name)

        every { eventRepo.createEvent(request.name, request.year) } returns event

        assertEquals(eventAdapter.add(request), event.toEvent())

        verify { eventRepo.createEvent(request.name, request.year) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `exists should call repo and return a Boolean`(exists: Boolean) {
        val id = Random.nextInt().absoluteValue
        every { eventRepo.existsById(id) } returns (exists)

        assertEquals(eventAdapter.exists(id), exists)

        verify { eventRepo.existsById(id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipants when result not empty, and return Optional of Event`(
        exists: Boolean
    ) {
        val event = generateRandomEventDB()
        val option = if (exists) Optional.of(event) else Optional.empty()
        every { eventRepo.findById(event.id) } returns option
        every { eventRepo.getParticipants(event.id) } returns emptySet()

        val result = eventAdapter.getById(event.id)

        verify { eventRepo.findById(event.id) }

        if (exists) {
            assertEquals(result, Optional.of(event.toEvent()))

            verify { eventRepo.getParticipants(event.id) }
        } else {
            val emptyOptional: Optional<Event> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { eventRepo.getParticipants(event.id) wasNot Called }
        }
    }

    @Test
    fun `update should transmit id and exploded EventPartial to repo and return the Event`() {
        val name = generateRandomHexString()
        val website = generateRandomHexString(3)

        val partial = EventPartial.empty().copy(name = name, website = website)
        val eventDB = generateRandomEventDB().copy(name = name, website = website)

        every { eventRepo.updateInfos(
            id = eventDB.id,
            name = partial.name,
            year = partial.year,
            debutEvent = partial.debutEvent,
            finEvent = partial.finEvent,
            debutCFP = partial.debutCFP,
            finCFP = partial.finCFP,
            debutInscription = partial.debutInscription,
            finInscription = partial.finInscription,
            website = partial.website
        ) } returns Unit
        every { eventRepo.findById(eventDB.id) } returns Optional.of(eventDB)
        every { eventRepo.getParticipants(eventDB.id) } returns emptySet()

        assertEquals(eventAdapter.update(eventDB.id, partial), eventDB.toEvent())

        verify { eventRepo.updateInfos(
            id = eventDB.id,
            name = partial.name,
            year = partial.year,
            debutEvent = partial.debutEvent,
            finEvent = partial.finEvent,
            debutCFP = partial.debutCFP,
            finCFP = partial.finCFP,
            debutInscription = partial.debutInscription,
            finInscription = partial.finInscription,
            website = partial.website
        ) }
        verify { eventRepo.findById(eventDB.id) }
        verify { eventRepo.getParticipants(eventDB.id) }
    }

    @Test
    fun `delete should call repo`() {
        val id = Random.nextInt().absoluteValue
        every { eventRepo.deleteById(id) } just Runs

        eventAdapter.delete(id)

        verify { eventRepo.deleteById(id) }
    }

}