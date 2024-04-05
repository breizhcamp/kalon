package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberAdd::class)
class MemberAddTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberAdd: MemberAdd

    private val testCreationReq = MemberCreationReq(
        lastname = "LUCAS",
        firstname = "Claire"
    )

    private val testMember = Member(
        id = UUID.randomUUID(),
        lastname = testCreationReq.lastname,
        firstname = testCreationReq.firstname,
        contacts = emptySet(),
        profilePictureLink = null,
        participations = emptySet()
    )

    @Test
    fun `should call port to add member`() {
        every { memberPort.add(testCreationReq) } returns testMember

        assertEquals(memberAdd.add(testCreationReq), testMember)

        verify { memberPort.add(testCreationReq) }
    }
}