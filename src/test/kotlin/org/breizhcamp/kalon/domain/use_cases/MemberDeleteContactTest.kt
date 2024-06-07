package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberDeleteContact::class)
class MemberDeleteContactTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberDeleteContact: MemberDeleteContact

    @Test
    fun `delete should call port and return the Member`() {
        val member = generateRandomMember()
        val contactId = UUID.randomUUID()

        every { memberPort.removeContact(member.id, contactId) } returns member

        assertEquals(memberDeleteContact.delete(member.id, contactId), member)

        verify { memberPort.removeContact(member.id, contactId) }
    }
}