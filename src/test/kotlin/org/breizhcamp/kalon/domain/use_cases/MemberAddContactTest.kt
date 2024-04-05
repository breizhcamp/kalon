package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Contact
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
@WebMvcTest(MemberAddContact::class)
class MemberAddContactTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberAddContact: MemberAddContact

    private val testMemberBefore = Member(
        id = UUID.randomUUID(),
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        profilePictureLink = null,
        participations = emptySet()
    )

    private val contact = Contact(
        id = UUID.randomUUID(),
        platform = "Example",
        link = "example.org/cllucas"
    )

    private val testMemberAfter = testMemberBefore.copy(
        contacts = setOf(contact)
    )

    @Test
    fun `should call port to add contact`() {
        every { memberPort.addContact(
            id = testMemberBefore.id,
            platform = contact.platform,
            link = contact.link
        ) } returns testMemberAfter

        assertEquals(memberAddContact.addContact(
            id = testMemberBefore.id,
            platform = contact.platform,
            link = contact.link
        ), testMemberAfter)

        verify { memberPort.addContact(
            id = testMemberBefore.id,
            platform = contact.platform,
            link = contact.link
        ) }
    }
}