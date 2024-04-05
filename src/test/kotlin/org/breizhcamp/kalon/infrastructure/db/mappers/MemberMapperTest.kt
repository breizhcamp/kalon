package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.MemberParticipation
import org.breizhcamp.kalon.infrastructure.db.model.ContactDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class MemberMapperTest {

    @Test
    fun `toMember should transmit all values, convert contacts and create an empty set for participations`() {
        val memberId = UUID.randomUUID()
        val contactDB = ContactDB(UUID.randomUUID(), memberId, generateRandomHexString(), generateRandomHexString())

        val memberDB = MemberDB(
            id = memberId,
            lastname = generateRandomHexString(),
            firstname = generateRandomHexString(),
            contacts = setOf(contactDB),
            profilePictureLink = generateRandomHexString(2)
        )
        val emptyParticipationSet: Set<MemberParticipation> = emptySet()

        val member = memberDB.toMember()

        assertEquals(member.id, memberDB.id)
        assertEquals(member.lastname, memberDB.lastname)
        assertEquals(member.firstname, memberDB.firstname)
        assertEquals(member.contacts, setOf(contactDB.toContact()))
        assertEquals(member.profilePictureLink, memberDB.profilePictureLink)
        assertEquals(member.participations, emptyParticipationSet)
    }

}