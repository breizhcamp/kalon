package org.breizhcamp.kalon.application.handlers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.EventCrud
import org.breizhcamp.kalon.domain.use_cases.MemberExists
import org.breizhcamp.kalon.domain.use_cases.ParticipationExists
import org.breizhcamp.kalon.domain.use_cases.TeamExists
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(HandleNotFound::class)
class HandleNotFoundTest {

    @MockkBean
    private lateinit var teamExists: TeamExists

    @MockkBean
    private lateinit var memberExists: MemberExists

    @MockkBean
    private lateinit var eventCrud: EventCrud

    @MockkBean
    private lateinit var participationExists: ParticipationExists

    @Autowired
    private lateinit var handleNotFound: HandleNotFound

    @Test
    fun `should return false and not log if member exists`(output: CapturedOutput) {
        val memberId = UUID.randomUUID()
        every { memberExists.exists(memberId) } returns true

        assert(!handleNotFound.memberNotFound(memberId))
        assert(!output.contains(notFoundLogPattern("Member", uuidRegex)))

        verify { memberExists.exists(memberId) }
    }

    @Test
    fun `should return true and log if member does not exist`(output: CapturedOutput) {
        val memberId = UUID.randomUUID()
        every { memberExists.exists(memberId) } returns false

        assert(handleNotFound.memberNotFound(memberId))
        assert(output.contains(notFoundLogPattern("Member", uuidRegex)))

        verify { memberExists.exists(memberId) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1])
    fun `should return true and log if any does not exist`(index: Int, output: CapturedOutput) {
        val memberId = UUID.randomUUID()
        val contactId = UUID.randomUUID()

        every { memberExists.exists(memberId) } returns (index != 0)
        every { memberExists.contactExists(memberId, contactId) } returns (index != 1)

        assert(handleNotFound.contactNotFound(memberId, contactId))
        when (index) {
            0 -> assert(output.contains(notFoundLogPattern("Member", uuidRegex)))
            1 -> assert(output.contains(Regex("Contact:$uuidRegex for Member:$uuidRegex not in database, returning a NOT_FOUND response")))
        }

        verify { memberExists.exists(memberId) }
        verify(exactly = if (index == 0) 0 else 1) { memberExists.contactExists(memberId, contactId) }
    }

    @Test
    fun `should return false and not log if member and contact exist`(output: CapturedOutput) {
        val memberId = UUID.randomUUID()
        val contactId = UUID.randomUUID()

        every { memberExists.exists(memberId) } returns true
        every { memberExists.contactExists(memberId, contactId) } returns true

        assert(!handleNotFound.contactNotFound(memberId, contactId))

        verify { memberExists.exists(memberId) }
        verify { memberExists.contactExists(memberId, contactId) }
    }

    @Test
    fun `should return false and not log if team exists`(output: CapturedOutput) {
        val teamId = UUID.randomUUID()

        every { teamExists.exists(teamId) } returns true

        assert(!handleNotFound.teamNotFound(teamId))
        assert(!output.contains(notFoundLogPattern("Team", uuidRegex)))

        verify { teamExists.exists(teamId) }
    }

    @Test
    fun `should return true and log if team does not exist`(output: CapturedOutput) {
        val teamId = UUID.randomUUID()

        every { teamExists.exists(teamId) } returns false

        assert(handleNotFound.teamNotFound(teamId))
        assert(output.contains(notFoundLogPattern("Team", uuidRegex)))

        verify { teamExists.exists(teamId) }
    }

    @Test
    fun `should return false and not log if event exists`(output: CapturedOutput) {
        val eventId = Random.nextInt()

        every { eventCrud.exists(eventId) } returns true

        assert(!handleNotFound.eventNotFound(eventId))
        assert(!output.contains(notFoundLogPattern("Event", integerRegex)))

        verify { eventCrud.exists(eventId) }
    }

    @Test
    fun `should return true and log if event does not exist`(output: CapturedOutput) {
        val eventId = Random.nextInt()

        every { eventCrud.exists(eventId) } returns false

        assert(handleNotFound.eventNotFound(eventId))
        assert(output.contains(notFoundLogPattern("Event", integerRegex)))

        verify { eventCrud.exists(eventId) }
    }

    @Test
    fun `should return false and not log if participation exists`(output: CapturedOutput) {
        every { participationExists.exists(any(), any(), any()) } returns true

        assert(!handleNotFound.participationNotFound(UUID.randomUUID(), UUID.randomUUID(), 1))
        assert(!output.contains(notFoundLogPattern("Participation", null)))

        verify { participationExists.exists(any(), any(), any()) }
    }

    @Test
    fun `should return true and log if participation does not exist`(output: CapturedOutput) {
        every { participationExists.exists(any(), any(), any()) } returns false

        assert(handleNotFound.participationNotFound(UUID.randomUUID(), UUID.randomUUID(), 1))
        assert(output.contains(notFoundLogPattern("Participation", null)))

        verify { participationExists.exists(any(), any(), any()) }
    }

    private val uuidRegex = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}"
    private val integerRegex = "[-+]*[0-9]+"

    private fun notFoundLogPattern(obj: String, regex: String?) = Regex("${obj}${
        if (regex == null) "" else ":$regex"
    } not in database, returning a NOT_FOUND response")

}