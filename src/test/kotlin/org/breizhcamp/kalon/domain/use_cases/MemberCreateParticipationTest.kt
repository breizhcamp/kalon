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
@WebMvcTest(MemberCreateParticipation::class)
class MemberCreateParticipationTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var memberCreateParticipation: MemberCreateParticipation

    private val id = UUID.randomUUID()
    private val teamId = UUID.randomUUID()
    private val eventId = 1

    @Test
    fun `should call port to create participation`() {
        val member = generateRandomMember()

        every { participationPort.createByIds(teamId, id, eventId) } just Runs
        every { memberPort.getById(id) } returns Optional.of(member)

        assertEquals(memberCreateParticipation.createParticipation(id, teamId, eventId), member)

        verify { participationPort.createByIds(teamId, id, eventId) }
        verify { memberPort.getById(id) }
    }
}