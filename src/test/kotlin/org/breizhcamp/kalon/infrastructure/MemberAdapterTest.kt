package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.requests.ContactCreationReq
import org.breizhcamp.kalon.application.requests.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.infrastructure.db.mappers.toMember
import org.breizhcamp.kalon.infrastructure.db.repos.MemberRepo
import org.breizhcamp.kalon.testUtils.generateRandomContactDB
import org.breizhcamp.kalon.testUtils.generateRandomMemberDB
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
        val returned = listOf(
            generateRandomMemberDB(),
            generateRandomMemberDB(),
            generateRandomMemberDB()
        )

        every { memberRepo.filter(filter) } returns returned

        assertEquals(memberAdapter.list(filter), returned.map { it.toMember() })

        verify { memberRepo.filter(filter) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipations when result not empty, and return Optional of Member`(
        exists: Boolean
    ) {
        val memberDB = generateRandomMemberDB()
        val option = if (exists) Optional.of(memberDB) else Optional.empty()

        every { memberRepo.findById(memberDB.id) } returns option
        every { memberRepo.getParticipations(memberDB.id) } returns emptySet()
        every { memberRepo.getPublicContacts(memberDB.id) } returns memberDB.contacts

        val result = memberAdapter.getById(memberDB.id)

        verify { memberRepo.findById(memberDB.id) }

        if (exists) {
            assertEquals(result, Optional.of(memberDB.toMember()))

            verify { memberRepo.getParticipations(memberDB.id) }
            verify { memberRepo.getPublicContacts(memberDB.id) }
        } else {
            val emptyOptional: Optional<Member> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { memberRepo.getParticipations(memberDB.id) wasNot Called }
            verify { memberRepo.getPublicContacts(memberDB.id) wasNot Called }
        }
    }

    @Test
    fun `add should call repo with values in request and return the Member`() {
        val memberDB = generateRandomMemberDB()
        val request = MemberCreationReq(memberDB.lastname, memberDB.firstname)

        every { memberRepo.createMember(request.lastname, request.firstname) } returns memberDB

        assertEquals(memberAdapter.add(request), memberDB.toMember())

        verify { memberRepo.createMember(request.lastname, request.firstname) }
    }

    @Test
    fun `update should transmit id and exploded MemberPartial to repo and return the Member`() {

        val memberDB = generateRandomMemberDB()
        val partial = MemberPartial.empty().copy(lastname = memberDB.lastname)

        every { memberRepo.updatePartial(
            id = memberDB.id,
            lastname = partial.lastname,
            firstname = partial.firstname,
            profilePictureLink = partial.profilePictureLink
        ) } returns Unit
        every { memberRepo.findById(memberDB.id) } returns Optional.of(memberDB)
        every { memberRepo.getParticipations(memberDB.id) } returns emptySet()
        every { memberRepo.getPublicContacts(memberDB.id) } returns emptySet()

        assertEquals(memberAdapter.update(memberDB.id, partial), memberDB.toMember())

        verify { memberRepo.updatePartial(
            id = memberDB.id,
            lastname = partial.lastname,
            firstname = partial.firstname,
            profilePictureLink = partial.profilePictureLink
        ) }
        verify { memberRepo.findById(memberDB.id) }
        verify { memberRepo.getParticipations(memberDB.id) }
        verify { memberRepo.getPublicContacts(memberDB.id) }
    }

    @Test
    fun `addContact should call repo with contact values and return the updated Member`() {
        val id = UUID.randomUUID()
        val contact = generateRandomContactDB(id)
        val req = ContactCreationReq(
            platform = contact.platform,
            link = contact.link,
            public = contact.public
        )
        val member = generateRandomMemberDB().copy(id = id, contacts = setOf(contact))

        every { memberRepo.addContact(id, req.platform, req.link, req.public) } returns Unit
        every { memberRepo.findById(id) } returns Optional.of(member)
        every { memberRepo.getParticipations(id) } returns emptySet()
        every { memberRepo.getPublicContacts(id) } returns setOf(contact)

        assertEquals(memberAdapter.addContact(id, req), member.toMember())

        verify { memberRepo.addContact(id, req.platform, req.link, req.public) }
        verify { memberRepo.findById(id) }
        verify { memberRepo.getParticipations(id) }
        verify { memberRepo.getPublicContacts(id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `existsById should call repo and return a Boolean`(exists: Boolean) {
        val id = UUID.randomUUID()
        every { memberRepo.existsById(id) } returns exists

        assertEquals(memberAdapter.existsById(id), exists)

        verify { memberRepo.existsById(id) }
    }

}