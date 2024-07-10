package org.breizhcamp.kalon.infrastructure

import jakarta.transaction.Transactional
import org.breizhcamp.kalon.application.requests.ContactCreationReq
import org.breizhcamp.kalon.application.requests.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Contact
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.breizhcamp.kalon.infrastructure.db.mappers.toContact
import org.breizhcamp.kalon.infrastructure.db.mappers.toMember
import org.breizhcamp.kalon.infrastructure.db.mappers.toParticipation
import org.breizhcamp.kalon.infrastructure.db.repos.MemberRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class MemberAdapter (
    private val memberRepo: MemberRepo
): MemberPort {

    override fun list(filter: MemberFilter): List<Member> =
        memberRepo.filter(filter).map { it.toMember() }

    override fun getById(id: UUID): Optional<Member> {
        val baseMemberDB = memberRepo.findById(id)

        if (baseMemberDB.isEmpty) return Optional.empty()

        val participations = memberRepo.getParticipations(id)
        var member = baseMemberDB.get().toMember()
        member.participations = participations.map { it.toParticipation() }.toSet()

        member = member.copy(contacts =
            memberRepo.getPublicContacts(id)
                .map { it.toContact() }
                .toSet()
        )

        return Optional.of(member)
    }

    override fun getKeycloakId(id: UUID): UUID? =
        memberRepo.getKeycloakIdById(id)

    @Transactional
    override fun add(creationReq: MemberCreationReq): Member =
        memberRepo.createMember(creationReq.lastname, creationReq.firstname).toMember()

    @Transactional
    override fun update(id: UUID, member: MemberPartial): Member {
        memberRepo.updatePartial(id, member.lastname, member.firstname, member.profilePictureLink)
        return getById(id).get()
    }

    override fun addContact(id: UUID, req: ContactCreationReq): Member {
        memberRepo.addContact(id, req.platform, req.link, req.public)
        return getById(id).get()
    }

    override fun getPrivateContacts(id: UUID): List<Contact> =
        memberRepo.getPrivateContacts(id).map{ it.toContact() }


    @Transactional
    override fun removeContact(id: UUID, contactId: UUID): Member {
        memberRepo.removeContact(contactId)
        return getById(id).get()
    }

    override fun existsById(id: UUID): Boolean =
        memberRepo.existsById(id)

    override fun contactExistsById(id: UUID, contactId: UUID): Boolean =
        memberRepo.existsContactByIds(id, contactId)

}
