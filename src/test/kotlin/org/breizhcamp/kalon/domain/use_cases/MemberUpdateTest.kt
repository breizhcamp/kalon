package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberUpdate::class)
class MemberUpdateTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberUpdate: MemberUpdate

    private val testMemberBefore = Member(
        id = UUID.randomUUID(),
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        profilePictureLink = null,
        participations = emptySet()
    )

    private val testMemberPartial = MemberPartial(
        lastname = null,
        firstname = null,
        profilePictureLink = "TEST"
    )

    private val testMemberAfter = testMemberBefore.copy(
        profilePictureLink = "TEST"
    )

    @Test
    fun `should call port to update`() {
        every { memberPort.update(testMemberBefore.id, testMemberPartial) } returns testMemberAfter

        assertEquals(memberUpdate.update(testMemberBefore.id, testMemberPartial), testMemberAfter)

        verify { memberPort.update(testMemberBefore.id, testMemberPartial) }
    }
}