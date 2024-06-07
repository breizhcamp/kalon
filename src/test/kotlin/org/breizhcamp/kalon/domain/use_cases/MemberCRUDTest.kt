package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.testUtils.generateRandomMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Optional

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberCRUD::class)
class MemberCRUDTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberCRUD: MemberCRUD

    @Test
    fun `add should call port with the request and return the Member`() {
        val member = generateRandomMember()
        val request = MemberCreationReq(lastname = member.lastname, firstname = member.firstname)

        every { memberPort.add(request) } returns member

        assertEquals(member, memberCRUD.add(request))

        verify { memberPort.add(request) }
    }

    @Test
    fun `list should call port with the filter and return the List of Member`() {
        val returned = listOf(
            generateRandomMember(),
            generateRandomMember(),
            generateRandomMember()
        )
        val filter = MemberFilter.empty()

        every { memberPort.list(filter) } returns returned

        assertEquals(returned, memberCRUD.list(filter))

        verify { memberPort.list(filter) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `getById should call port with the id and return the Optional of Member`(exists: Boolean) {
        val member = generateRandomMember()

        every { memberPort.getById(member.id) } returns
                if (exists) Optional.of(member)
                else Optional.empty()

        val result = memberCRUD.getById(member.id)
        assertEquals(exists, result.isPresent)
        if (exists) {
            assertEquals(member, result.get())
        }

        verify { memberPort.getById(member.id) }
    }

    @Test
    fun `update should call port with Member Id and Partial, and return the updated Member`() {
        val member = generateRandomMember()
        val partial = MemberPartial(lastname = member.lastname, firstname = member.lastname, profilePictureLink = member.profilePictureLink)
        every { memberPort.update(member.id, partial) } returns member

        assertEquals(member, memberCRUD.update(member.id, partial))

        verify { memberPort.update(member.id, partial) }
    }
}