package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.infrastructure.db.mappers.toEvent
import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.breizhcamp.kalon.infrastructure.db.repos.EventRepo
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
        val returned = listOf(0, 1, 2, 3).map(this::testEventDB)

        every { eventRepo.filter(filter) } returns returned

        assertEquals(eventAdapter.list(filter), returned.map { it.toEvent() })

        verify { eventRepo.filter(filter) }
    }

    @Test
    fun `add should call repo with values in request and return the Event`() {
        val year = 2024
        val name = "Breizh camp 2024"

        val request = EventCreationReq(year, name)
        val event = testEventDB(1, year).copy(name = name)

        every { eventRepo.createEvent(name, year) } returns event

        assertEquals(eventAdapter.add(request), event.toEvent())

        verify { eventRepo.createEvent(name, year) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `exists should call repo and return a Boolean`(exists: Boolean) {
        val id = Random.nextInt(0, 10)
        every { eventRepo.existsById(id) } returns (exists)

        assertEquals(eventAdapter.exists(id), exists)

        verify { eventRepo.existsById(id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipants when result not empty, and return Optional of Event`(
        exists: Boolean
    ) {
        val id = Random.nextInt(0, 10)
        val option = if (exists) Optional.of(testEventDB(id)) else Optional.empty()
        every { eventRepo.findById(id) } returns option
        every { eventRepo.getParticipants(id) } returns emptySet()

        val result = eventAdapter.getById(id)

        verify { eventRepo.findById(id) }

        if (exists) {
            assertEquals(result, Optional.of(testEventDB(id).toEvent()))

            verify { eventRepo.getParticipants(id) }
        } else {
            val emptyOptional: Optional<Event> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { eventRepo.getParticipants(id) wasNot Called }
        }
    }

    @Test
    fun `updateInfos should transmit id and exploded EventPartial to repo and return the Event`() {
        val id = Random.nextInt(0, 10)
        val name = generateRandomHexString()
        val website = generateRandomHexString(3)

        val partial = EventPartial.empty().copy(name = name, website = website)
        val eventDB = testEventDB(id).copy(name = name, website = website)

        every { eventRepo.updateInfos(
            id = id,
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
        every { eventRepo.findById(id) } returns Optional.of(eventDB)
        every { eventRepo.getParticipants(id) } returns emptySet()

        assertEquals(eventAdapter.updateInfos(id, partial), eventDB.toEvent())

        verify { eventRepo.updateInfos(
            id = id,
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
        verify { eventRepo.findById(id) }
        verify { eventRepo.getParticipants(id) }
    }

    private fun testEventDB(id: Int, year: Int = 2020 + id) = EventDB(
        id = id,
        year = year,
        name = null,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        website = null
    )

}