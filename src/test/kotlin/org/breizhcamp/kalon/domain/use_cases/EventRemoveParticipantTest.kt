package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventRemoveParticipant::class)
class EventRemoveParticipantTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var eventRemoveParticipant: EventRemoveParticipant

    @Test
    fun `should call port to remove participant`() {
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()

        val event = generateRandomEvent()

        every { participationPort.removeByIds(teamId, memberId, event.id) } just Runs
        every { eventPort.getById(event.id) } returns Optional.of(event)

        assertEquals(eventRemoveParticipant.removeParticipant(event.id, memberId, teamId), event)

        verify { participationPort.removeByIds(teamId, memberId, event.id) }
        verify { eventPort.getById(event.id) }
    }
}