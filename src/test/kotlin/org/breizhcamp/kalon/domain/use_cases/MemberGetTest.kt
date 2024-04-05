package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
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
@WebMvcTest(MemberGet::class)
class MemberGetTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberGet: MemberGet

    private val testMember = Member(
        id = UUID.randomUUID(),
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        profilePictureLink = null,
        participations = emptySet()
    )

    @Test
    fun `should call port to get`() {
        every { memberPort.getById(testMember.id) } returns Optional.of(testMember)

        assert(memberGet.getById(testMember.id).isPresent)
        assertEquals(memberGet.getById(testMember.id).get(), testMember)

        verify { memberPort.getById(testMember.id) }
    }
}