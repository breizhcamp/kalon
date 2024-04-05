package org.breizhcamp.kalon.application.handlers

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.EventExists
import org.breizhcamp.kalon.domain.use_cases.MemberExists
import org.breizhcamp.kalon.domain.use_cases.ParticipationExists
import org.breizhcamp.kalon.domain.use_cases.TeamExists
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(HandleNotFound::class)
class HandleNotFoundTest {

    private val uuidRegex = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}"
    private val integerRegex = "[0-9]+"
    private fun notFoundLogPattern(obj: String, regex: String?) = Regex("${obj}${
        if (regex == null) "" else ":$regex"
    } not in database, returning a NOT_FOUND response")

    @MockkBean
    private lateinit var teamExists: TeamExists
    private val teamExistsId = UUID.randomUUID()
    private val teamDoesNotExistId = UUID.randomUUID()

    @MockkBean
    private lateinit var memberExists: MemberExists
    private val memberExistsId = UUID.randomUUID()
    private val memberDoesNotExistId = UUID.randomUUID()

    @MockkBean
    private lateinit var eventExists: EventExists
    private val eventExistsId = 1
    private val eventDoesNotExistId = 2

    @MockkBean
    private lateinit var participationExists: ParticipationExists

    @Autowired
    private lateinit var handleNotFound: HandleNotFound

    @Test
    fun `should return false and not log if member exists`(output: CapturedOutput) {
        every { memberExists.exists(memberExistsId) } returns true

        assert(!handleNotFound.memberNotFound(memberExistsId))
        assert(!output.contains(notFoundLogPattern("Member", uuidRegex)))

        verify { memberExists.exists(memberExistsId) }
    }

    @Test
    fun `should return true and log if member does not exist`(output: CapturedOutput) {
        every { memberExists.exists(memberDoesNotExistId) } returns false

        assert(handleNotFound.memberNotFound(memberDoesNotExistId))
        assert(output.contains(notFoundLogPattern("Member", uuidRegex)))

        verify { memberExists.exists(memberDoesNotExistId) }
    }

    @Test
    fun `should return false and not log if team exists`(output: CapturedOutput) {
        every { teamExists.exists(teamExistsId) } returns true

        assert(!handleNotFound.teamNotFound(teamExistsId))
        assert(!output.contains(notFoundLogPattern("Team", uuidRegex)))

        verify { teamExists.exists(teamExistsId) }
    }

    @Test
    fun `should return true and log if team does not exist`(output: CapturedOutput) {
        every { teamExists.exists(teamDoesNotExistId) } returns false

        assert(handleNotFound.teamNotFound(teamDoesNotExistId))
        assert(output.contains(notFoundLogPattern("Team", uuidRegex)))

        verify { teamExists.exists(teamDoesNotExistId) }
    }

    @Test
    fun `should return false and not log if event exists`(output: CapturedOutput) {
        every { eventExists.exists(eventExistsId) } returns true

        assert(!handleNotFound.eventNotFound(eventExistsId))
        assert(!output.contains(notFoundLogPattern("Event", integerRegex)))

        verify { eventExists.exists(eventExistsId) }
    }

    @Test
    fun `should return true and log if event does not exist`(output: CapturedOutput) {
        every { eventExists.exists(eventDoesNotExistId) } returns false

        assert(handleNotFound.eventNotFound(eventDoesNotExistId))
        assert(output.contains(notFoundLogPattern("Event", integerRegex)))

        verify { eventExists.exists(eventDoesNotExistId) }
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

}