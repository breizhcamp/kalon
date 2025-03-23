package org.breizhcamp.kalon.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.application.dto.EventDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.entities.EventParticipant
import org.breizhcamp.kalon.domain.entities.Order
import org.breizhcamp.kalon.domain.use_cases.EventAddParticipant
import org.breizhcamp.kalon.domain.use_cases.EventCrud
import org.breizhcamp.kalon.domain.use_cases.EventRemoveParticipant
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.breizhcamp.kalon.testUtils.generateRandomTeam
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(EventController::class)
class EventControllerTest {

    @MockkBean
    private lateinit var eventCrud: EventCrud

    @MockkBean
    private lateinit var eventAddParticipant: EventAddParticipant

    @MockkBean
    private lateinit var eventRemoveParticipant: EventRemoveParticipant

    @MockkBean
    private lateinit var handleNotFound: HandleNotFound

    @Autowired
    private lateinit var eventController: EventController


    @Test
    fun `list should log, call eventList with default filter and return the IDs`(
        output: CapturedOutput,
    ) {
        val returnedEvents = listOf(
            generateRandomEvent(),
            generateRandomEvent(),
            generateRandomEvent()
        )
        every { eventCrud.list(EventFilter.default()) } returns returnedEvents

        assertEquals(eventController.list(), returnedEvents.map { it.id })
        assert(output.contains(
            "Listing all Events by their IDs"
        ))

        verify { eventCrud.list(EventFilter.default()) }
    }

    @Test
    fun `createEvent should log, call eventAdd with request and return the EventDTO`(
        output: CapturedOutput,
    ) {
        val request = EventCreationReq(2024, null)
        val returnedEvent = generateRandomEvent().copy(year = request.year)
        every { eventCrud.create(request) } returns returnedEvent

        assertEquals(eventController.createEvent(request), returnedEvent.toDto())
        assert(output.contains(
            "Creating Event with values name=${request.name} year=${request.year}"
        ))

        verify { eventCrud.create(request) }
    }

    @Test
    fun `filterEvents should log, call eventList with the values and return the EventDTOs`(
        output: CapturedOutput
    ) {
        val filter = EventFilter.default().copy(yearBefore = 2023, yearOrder = Order.ASC)
        val returnedEvents = listOf(1, 2).map(this::testEvent)
        every { eventCrud.list(filter) } returns returnedEvents

        assertEquals(eventController.filterEvents(filter), returnedEvents.sortedBy { it.year }.map { it.toDto() })
        assert(output.contains(
            "Filtering ${filter.limit?:"all"} Events with ${filter.yearOrder} year order"
        ))

        verify { eventCrud.list(filter)}
    }

    @Test
    fun `getEventInfos should log, call handleNotFound and eventGet and return OK and the EventDTO if Event found`(
        output: CapturedOutput
    ) {
        val event = generateRandomEvent()
        every { handleNotFound.eventNotFound(event.id) } returns false
        every { eventCrud.get(event.id) } returns Optional.of(event)

        assertEquals(eventController.getEventInfos(event.id),  ResponseEntity.ok(event.toDto()))
        assert(output.contains(
            "Retrieving Event:${event.id}"
        ))

        verify { handleNotFound.eventNotFound(event.id) }
        verify { eventCrud.get(event.id) }
    }

    @Test
    fun `getEventInfos should log, call handleNotFound, not call eventGet and return a NOT_FOUND response if Event not found`(
        output: CapturedOutput
    ) {
        val id = Random.nextInt().absoluteValue
        every { handleNotFound.eventNotFound(any()) } returns true
        every { eventCrud.get(any()) }

        assertEquals(eventController.getEventInfos(id), ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Retrieving Event:${id}"
        ))

        verify { handleNotFound.eventNotFound(any()) }
        verify { eventCrud.get(any()) wasNot Called }
    }

    @Test
    fun `updateEventInfos should log, call handleNotFound and eventUpdateInfos and return OK and the EventDTO if the Event is found`(
        output: CapturedOutput
    ) {
        val id = 3
        val name = "Breizh camp 2024"
        val partial = EventPartial.empty().copy(name = name)
        val eventAfter = testEvent(id).copy(name = name)
        every { handleNotFound.eventNotFound(id) } returns false
        every { eventCrud.update(id, partial) } returns eventAfter

        assertEquals(eventController.updateEventInfos(id, partial.toDto()), ResponseEntity.ok(eventAfter.toDto()))
        assert(output.contains(
            "Updating infos of Event:${id}"
        ))

        verify { handleNotFound.eventNotFound(id) }
        verify { eventCrud.update(id, partial) }
    }

    @Test
    fun `updateEventInfos should log, call handleNotFound, not call eventUpdateInfos and return NOT_FOUND if Event not found`(
        output: CapturedOutput
    ) {
        val id = Random.nextInt().absoluteValue
        val partial = EventPartial.empty()
        every { handleNotFound.eventNotFound(any()) } returns true
        every { eventCrud.update(any(), any()) }

        assertEquals(eventController.updateEventInfos(id, partial.toDto()), ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Updating infos of Event:${id}"
        ))

        verify { handleNotFound.eventNotFound(any()) }
        verify { eventCrud.update(any(), any()) wasNot Called }
    }

    @Test
    fun `addParticipant should log, call handleNotFound and eventAddParticipant and return OK and the EventDTO if all found`(
        output: CapturedOutput
    ) {
        val team = generateRandomTeam()
        val member = generateRandomMember()

        val event = generateRandomEvent().copy(eventParticipants = setOf(EventParticipant(member, team)))

        every { handleNotFound.memberNotFound(member.id) } returns false
        every { handleNotFound.teamNotFound(team.id) } returns false
        every { handleNotFound.eventNotFound(event.id) } returns false
        every { eventAddParticipant.addParticipant(event.id, member.id, team.id) } returns event

        val response = eventController.addParticipant(event.id, member.id, team.id)
        assert(output.contains(
            "Adding Participant:<Member:${member.id}, Team:${team.id}> to Event:${event.id}"
        ))
        assertEquals(response, ResponseEntity.ok(event.toDto()))

        verify { handleNotFound.memberNotFound(member.id) }
        verify { handleNotFound.teamNotFound(team.id) }
        verify { handleNotFound.eventNotFound(event.id) }
        verify { eventAddParticipant.addParticipant(event.id, member.id, team.id) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `addParticipant should log, call handleNotFound, not call eventAddParticipant and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = 5
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()

        val event = testEvent(id)

        every { handleNotFound.memberNotFound(memberId) } returns (index==0)
        every { handleNotFound.teamNotFound(teamId) } returns (index==1)
        every { handleNotFound.eventNotFound(id) } returns (index==2)
        every { eventAddParticipant.addParticipant(id, memberId, teamId) } returns event

        assertEquals(
            eventController.addParticipant(id, memberId, teamId),
            ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404))
        )
        assert(output.contains(
            "Adding Participant:<Member:${memberId}, Team:${teamId}> to Event:${id}"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(teamId) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(id) }
        verify { eventAddParticipant.addParticipant(id, memberId, teamId) wasNot Called }
    }

    @Test
    fun `removeParticipant should log, call handleNotFound and eventRemoveParticipant and return OK with EventDTO if all found`(
        output: CapturedOutput
    ) {
        val id = Random.nextInt().absoluteValue
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()

        val event = testEvent(id)

        every { handleNotFound.memberNotFound(memberId) } returns false
        every { handleNotFound.teamNotFound(teamId) } returns false
        every { handleNotFound.eventNotFound(id) } returns false
        every { handleNotFound.participationNotFound(teamId, memberId, id) } returns false
        every { eventRemoveParticipant.removeParticipant(id, memberId, teamId) } returns event

        assertEquals(eventController.removeParticipant(id, memberId, teamId), ResponseEntity.ok(event.toDto()))
        assert(output.contains(
            "Removing Participant:<Member:${memberId}, Team:${teamId}> from Event:${id}"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify { handleNotFound.teamNotFound(teamId) }
        verify { handleNotFound.eventNotFound(id) }
        verify { handleNotFound.participationNotFound(teamId, memberId, id) }
        verify { eventRemoveParticipant.removeParticipant(id, memberId, teamId) }
    }

    @ParameterizedTest
    @ValueSource(ints=[0, 1, 2, 3])
    fun `removeParticipant should log, call handleNotFound, not call eventRemoveParticipant and return NOT_FOUND if any not found`(
        index: Int,
        output: CapturedOutput
    ) {
        val id = Random.nextInt().absoluteValue
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()

        every { handleNotFound.memberNotFound(memberId) } returns (index==0)
        every { handleNotFound.teamNotFound(teamId) } returns (index==1)
        every { handleNotFound.eventNotFound(id) } returns (index==2)
        every { handleNotFound.participationNotFound(teamId, memberId, id) } returns (index==3)
        every { eventRemoveParticipant.removeParticipant(id, memberId, teamId) }

        assertEquals(eventController.removeParticipant(id, memberId, teamId), ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Removing Participant:<Member:${memberId}, Team:${teamId}> from Event:${id}"
        ))

        verify { handleNotFound.memberNotFound(memberId) }
        verify(exactly = if (index <= 0) 0 else 1) { handleNotFound.teamNotFound(teamId) }
        verify(exactly = if (index <= 1) 0 else 1) { handleNotFound.eventNotFound(id) }
        verify(exactly = if (index <= 2) 0 else 1) { handleNotFound.participationNotFound(teamId, memberId, id) }
        verify { eventRemoveParticipant.removeParticipant(id, memberId, teamId) wasNot Called }
    }

    private fun testEvent(id: Int, year: Int = 2020 + id) = generateRandomEvent().copy(id = id, year = year)
}