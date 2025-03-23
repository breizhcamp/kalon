package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.EventParticipant
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.breizhcamp.kalon.testUtils.generateRandomTeam
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

    @Test
    fun `should call port to add participant`() {
        val eventBefore = generateRandomEvent()

        val team = generateRandomTeam()
        val member = generateRandomMember()

        val eventAfter = eventBefore.copy(eventParticipants = setOf(EventParticipant(member, team)))

        every { participationPort.createByIds(team.id, member.id, eventBefore.id) } just Runs
        every { eventPort.getById(eventAfter.id) } returns Optional.of(eventAfter)

        Assertions.assertEquals(eventAddParticipant.addParticipant(eventBefore.id, member.id, team.id), eventAfter)

        verify { participationPort.createByIds(team.id, member.id, eventBefore.id) }
        verify { eventPort.getById(eventAfter.id) }
    }
}