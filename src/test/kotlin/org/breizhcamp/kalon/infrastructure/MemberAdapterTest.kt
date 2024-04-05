package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.infrastructure.db.mappers.toMember
import org.breizhcamp.kalon.infrastructure.db.model.ContactDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.breizhcamp.kalon.infrastructure.db.repos.MemberRepo
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberAdapter::class)
class MemberAdapterTest {

    @MockkBean
    private lateinit var memberRepo: MemberRepo

    @Autowired
    private lateinit var memberAdapter: MemberAdapter

    @Test
    fun `list should call repo with provided filter and return a list of Members`() {
        val filter = MemberFilter.empty()
        val returned = listOf(0..2).map { this.testMemberDB() }

        every { memberRepo.filter(filter) } returns returned

        assertEquals(memberAdapter.list(filter), returned.map { it.toMember() })

        verify { memberRepo.filter(filter) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipations when result not empty, and return Optional of Member`(
        exists: Boolean
    ) {
        val id = UUID.randomUUID()
        val memberDB = testMemberDB().copy(id = id)
        val option = if (exists) Optional.of(memberDB) else Optional.empty()
        every { memberRepo.findById(id) } returns option
        every { memberRepo.getParticipations(id) } returns emptySet()

        val result = memberAdapter.getById(id)

        verify { memberRepo.findById(id) }

        if (exists) {
            assertEquals(result, Optional.of(memberDB.toMember()))

            verify { memberRepo.getParticipations(id) }
        } else {
            val emptyOptional: Optional<Member> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { memberRepo.getParticipations(id) wasNot Called }
        }
    }

    @Test
    fun `add should call repo with values in request and return the Member`() {
        val lastname = generateRandomHexString()
        val firstname = generateRandomHexString()

        val request = MemberCreationReq(lastname, firstname)
        val memberDB = testMemberDB().copy(lastname = lastname, firstname = firstname)

        every { memberRepo.createMember(lastname, firstname) } returns memberDB

        assertEquals(memberAdapter.add(request), memberDB.toMember())

        verify { memberRepo.createMember(lastname, firstname) }
    }

    @Test
    fun `update should transmit id and exploded MemberPartial to repo and return the Member`() {
        val id = UUID.randomUUID()
        val lastname = generateRandomHexString()
        val firstname = generateRandomHexString()

        val partial = MemberPartial(
            lastname = lastname,
            firstname = firstname,
            profilePictureLink = null
        )
        val memberDB = MemberDB(
            id = id,
            lastname = lastname,
            firstname = firstname,
            contacts = emptySet(),
            profilePictureLink = null
        )

        every { memberRepo.updatePartial(
            id = id,
            lastname = partial.lastname,
            firstname = partial.firstname,
            profilePictureLink = partial.profilePictureLink
        ) } returns Unit
        every { memberRepo.findById(id) } returns Optional.of(memberDB)
        every { memberRepo.getParticipations(id) } returns emptySet()

        assertEquals(memberAdapter.update(id, partial), memberDB.toMember())

        verify { memberRepo.updatePartial(
            id = id,
            lastname = lastname,
            firstname = firstname,
            profilePictureLink = null
        ) }
        verify { memberRepo.findById(id) }
        verify { memberRepo.getParticipations(id) }
    }

    @Test
    fun `addContact should call repo with contact values and return the updated Member`() {
        val id = UUID.randomUUID()
        val platform = generateRandomHexString()
        val link = generateRandomHexString(2)

        val contact = ContactDB(
            id = UUID.randomUUID(),
            memberId = id,
            platform = platform,
            link = link
        )
        val member = MemberDB(
            id = id,
            lastname = generateRandomHexString(),
            firstname = generateRandomHexString(),
            contacts = setOf(contact),
            profilePictureLink = null
        )

        every { memberRepo.addContact(id, platform, link) } returns Unit
        every { memberRepo.findById(id) } returns Optional.of(member)
        every { memberRepo.getParticipations(id) } returns emptySet()

        assertEquals(memberAdapter.addContact(id, platform, link), member.toMember())

        verify { memberRepo.addContact(id, platform, link) }
        verify { memberRepo.findById(id) }
        verify { memberRepo.getParticipations(id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `existsById should call repo and return a Boolean`(exists: Boolean) {
        val id = UUID.randomUUID()
        every { memberRepo.existsById(id) } returns exists

        assertEquals(memberAdapter.existsById(id), exists)

        verify { memberRepo.existsById(id) }
    }

    private fun testMemberDB(): MemberDB {
        return MemberDB(
            id = UUID.randomUUID(),
            lastname = generateRandomHexString(),
            firstname = generateRandomHexString(),
            contacts = emptySet(),
            profilePictureLink = null,
        )
    }

}