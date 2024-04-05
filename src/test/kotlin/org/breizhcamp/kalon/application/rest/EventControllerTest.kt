package org.breizhcamp.kalon.application.rest

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.application.dto.EventDTO
import org.breizhcamp.kalon.application.handlers.HandleNotFound
import org.breizhcamp.kalon.domain.entities.*
import org.breizhcamp.kalon.domain.use_cases.*
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

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(EventController::class)
class EventControllerTest {

    @MockkBean
    private lateinit var eventList: EventList

    @MockkBean
    private lateinit var eventAdd: EventAdd

    @MockkBean
    private lateinit var eventGet: EventGet

    @MockkBean
    private lateinit var eventAddParticipant: EventAddParticipant

    @MockkBean
    private lateinit var eventRemoveParticipant: EventRemoveParticipant

    @MockkBean
    private lateinit var eventUpdateInfos: EventUpdateInfos

    @MockkBean
    private lateinit var handleNotFound: HandleNotFound

    @Autowired
    private lateinit var eventController: EventController


    @Test
    fun `list should log, call eventList with default filter and return the IDs`(
        output: CapturedOutput,
    ) {
        val returnedEvents = listOf(1, 2, 4).map(this::testEvent)
        every { eventList.list(EventFilter.default()) } returns returnedEvents

        assertEquals(eventController.list(), returnedEvents.map { it.id })
        assert(output.contains(
            "Listing all Events by their IDs"
        ))

        verify { eventList.list(EventFilter.default()) }
    }

    @Test
    fun `createEvent should log, call eventAdd with request and return the EventDTO`(
        output: CapturedOutput,
    ) {
        val request = EventCreationReq(2024, null)
        every { eventAdd.add(request) } returns testEvent(1, 2024)

        assertEquals(eventController.createEvent(request), testEvent(1, 2024).toDto())
        assert(output.contains(
            "Creating Event with values name=${request.name} year=${request.year}"
        ))

        verify { eventAdd.add(request) }
    }

    @Test
    fun `filterEvents should log, call eventList with the values and return the EventDTOs`(
        output: CapturedOutput
    ) {
        val filter = EventFilter.default().copy(yearBefore = 2023, yearOrder = Order.ASC)
        val returnedEvents = listOf(1, 2).map(this::testEvent)
        every { eventList.list(filter) } returns returnedEvents

        assertEquals(eventController.filterEvents(filter), returnedEvents.sortedBy { it.year }.map { it.toDto() })
        assert(output.contains(
            "Filtering ${filter.limit?:"all"} Events with ${filter.yearOrder} year order"
        ))

        verify { eventList.list(filter)}
    }

    @Test
    fun `getEventInfos should log, call handleNotFound and eventGet and return OK and the EventDTO if Event found`(
        output: CapturedOutput
    ) {
        val id = 1
        every { handleNotFound.eventNotFound(id) } returns false
        every { eventGet.get(id) } returns Optional.of(testEvent(id))

        assertEquals(eventController.getEventInfos(id),  ResponseEntity.ok(testEvent(id).toDto()))
        assert(output.contains(
            "Retrieving Event:${id}"
        ))

        verify { handleNotFound.eventNotFound(id) }
        verify { eventGet.get(id) }
    }

    @Test
    fun `getEventInfos should log, call handleNotFound, not call eventGet and return a NOT_FOUND response if Event not found`(
        output: CapturedOutput
    ) {
        every { handleNotFound.eventNotFound(any()) } returns true
        every { eventGet.get(any()) }

        assertEquals(eventController.getEventInfos(1), ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Retrieving Event:${1}"
        ))

        verify { handleNotFound.eventNotFound(any()) }
        verify { eventGet.get(any()) wasNot Called }
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
        every { eventUpdateInfos.updateInfos(id, partial) } returns eventAfter

        assertEquals(eventController.updateEventInfos(id, partial.toDto()), ResponseEntity.ok(eventAfter.toDto()))
        assert(output.contains(
            "Updating infos of Event:${id}"
        ))

        verify { handleNotFound.eventNotFound(id) }
        verify { eventUpdateInfos.updateInfos(id, partial) }
    }

    @Test
    fun `updateEventInfos should log, call handleNotFound, not call eventUpdateInfos and return NOT_FOUND if Event not found`(
        output: CapturedOutput
    ) {
        val partial = EventPartial.empty()
        every { handleNotFound.eventNotFound(any()) } returns true
        every { eventUpdateInfos.updateInfos(any(), any()) }

        assertEquals(eventController.updateEventInfos(1, partial.toDto()), ResponseEntity<EventDTO>(HttpStatusCode.valueOf(404)))
        assert(output.contains(
            "Updating infos of Event:${1}"
        ))

        verify { handleNotFound.eventNotFound(any()) }
        verify { eventUpdateInfos.updateInfos(any(), any()) wasNot Called }
    }

    @Test
    fun `addParticipant should log, call handleNotFound and eventAddParticipant and return OK and the EventDTO if all found`(
        output: CapturedOutput
    ) {
        val id = 4
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()

        val team = Team(teamId, "Orga", null, emptySet())
        val member = Member(memberId, "LUCAS", "Claire", emptySet(), null, emptySet())

        val event = testEvent(id).copy(eventParticipants = setOf(EventParticipant(member, team)))

        every { handleNotFound.memberNotFound(memberId) } returns false
        every { handleNotFound.teamNotFound(teamId) } returns false
        every { handleNotFound.eventNotFound(id) } returns false
        every { eventAddParticipant.addParticipant(id, memberId, teamId) } returns event

        val response = eventController.addParticipant(id, memberId, teamId)
        assert(output.contains(
            "Adding Participant:<Member:${memberId}, Team:${teamId}> to Event:${id}"
        ))
        assertEquals(response, ResponseEntity.ok(event.toDto()))

        verify { handleNotFound.memberNotFound(memberId) }
        verify { handleNotFound.teamNotFound(teamId) }
        verify { handleNotFound.eventNotFound(id) }
        verify { eventAddParticipant.addParticipant(id, memberId, teamId) }
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
        val id = 6
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
        val id = 7
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

    private fun testEvent(id: Int, year: Int = 2020 + id) = Event(
        id = id,
        name = null,
        year = year,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        eventParticipants = emptySet(),
        website = null
    )
}