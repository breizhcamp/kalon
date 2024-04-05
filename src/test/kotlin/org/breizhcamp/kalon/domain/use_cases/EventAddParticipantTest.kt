package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAddParticipant::class)
class EventAddParticipantTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var eventAddParticipant: EventAddParticipant

    private val memberId = UUID.randomUUID()
    private val teamId = UUID.randomUUID()

    private val testEvent = Event(
        id = 1,
        name = "Breizh camp 2024",
        year = 2024,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        eventParticipants = emptySet(),
        website = null,
    )

    @Test
    fun `should call port to add participant`() {
        every { participationPort.createByIds(teamId, memberId, 1) } just Runs
        every { eventPort.getById(1) } returns Optional.of(testEvent)

        Assertions.assertEquals(eventAddParticipant.addParticipant(1, memberId, teamId), testEvent)

        verify { participationPort.createByIds(teamId, memberId, 1) }
        verify { eventPort.getById(1) }
    }
}