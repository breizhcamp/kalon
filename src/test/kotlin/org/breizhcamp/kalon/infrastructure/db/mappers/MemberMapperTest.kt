package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.MemberParticipation
import org.breizhcamp.kalon.testUtils.generateRandomMemberDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MemberMapperTest {

    @Test
    fun `toMember should transmit all values, convert contacts and create an empty set for participations`() {
        val memberDB = generateRandomMemberDB()
        val member = memberDB.toMember()
        val emptyParticipationSet: Set<MemberParticipation> = emptySet()

        assertEquals(member.id, memberDB.id)
        assertEquals(member.lastname, memberDB.lastname)
        assertEquals(member.firstname, memberDB.firstname)
        assertEquals(member.contacts, memberDB.contacts.map { it.toContact() }.toSet())
        assertEquals(member.profilePictureLink, memberDB.profilePictureLink)
        assertEquals(member.participations, emptyParticipationSet)
    }

}