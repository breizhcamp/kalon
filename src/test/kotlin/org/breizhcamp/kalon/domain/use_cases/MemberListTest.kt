package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberList::class)
class MemberListTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberList: MemberList

    private val testMember = Member(
        id = UUID.randomUUID(),
        lastname = "LUCAS",
        firstname = "Claire",
        contacts = emptySet(),
        profilePictureLink = null,
        participations = emptySet()
    )

    @Test
    fun `should call port to list members`() {
        every { memberPort.list(MemberFilter.empty()) } returns listOf(testMember)

        assert(memberList.list(MemberFilter.empty()).contains(testMember))

        verify { memberPort.list(MemberFilter.empty()) }
    }
}