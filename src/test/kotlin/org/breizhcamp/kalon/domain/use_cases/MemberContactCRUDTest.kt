package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.requests.ContactCreationReq
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.testUtils.generateRandomContact
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberContactCRUD::class)
class MemberContactCRUDTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberContactCRUD: MemberContactCRUD

    @Test
    fun `should call port to add contact`() {
        val memberBefore = generateRandomMember()
        val contact = generateRandomContact()
        val req = ContactCreationReq(
            platform = contact.platform,
            link = contact.link,
            public = contact.public
        )
        val memberAfter = memberBefore.copy(contacts = setOf(contact))

        every { memberPort.addContact(
            id = memberBefore.id,
            req = req
        ) } returns memberAfter

        assertEquals(memberContactCRUD.create(
            id = memberBefore.id,
            req = req
        ), memberAfter)

        verify { memberPort.addContact(
            id = memberBefore.id,
            req = req
        ) }
    }

    @Test
    fun `delete should call port and return the Member`() {
        val member = generateRandomMember()
        val contactId = UUID.randomUUID()

        every { memberPort.removeContact(member.id, contactId) } returns member

        assertEquals(memberContactCRUD.delete(member.id, contactId), member)

        verify { memberPort.removeContact(member.id, contactId) }
    }
}