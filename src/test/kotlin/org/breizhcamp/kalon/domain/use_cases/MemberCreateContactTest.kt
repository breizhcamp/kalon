package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.testUtils.generateRandomContact
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberCreateContact::class)
class MemberCreateContactTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberCreateContact: MemberCreateContact

    @Test
    fun `should call port to add contact`() {
        val memberBefore = generateRandomMember()
        val contact = generateRandomContact()
        val memberAfter = memberBefore.copy(contacts = setOf(contact))

        every { memberPort.addContact(
            id = memberBefore.id,
            platform = contact.platform,
            link = contact.link
        ) } returns memberAfter

        assertEquals(memberCreateContact.create(
            id = memberBefore.id,
            platform = contact.platform,
            link = contact.link
        ), memberAfter)

        verify { memberPort.addContact(
            id = memberBefore.id,
            platform = contact.platform,
            link = contact.link
        ) }
    }
}