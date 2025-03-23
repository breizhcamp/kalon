package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberDeleteParticipation::class)
class MemberDeleteParticipationTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var memberDeleteParticipation: MemberDeleteParticipation

    @Test
    fun `should call port to delete participation`() {
        val teamId = UUID.randomUUID()
        val eventId = 1

        val member = generateRandomMember()

        every { participationPort.removeByIds(teamId, member.id, eventId) } just Runs
        every { memberPort.getById(member.id) } returns Optional.of(member)

        assertEquals(memberDeleteParticipation.deleteParticipation(member.id, teamId, eventId), member)

        verify { participationPort.removeByIds(teamId, member.id, eventId) }
        verify { memberPort.getById(member.id) }
    }
}