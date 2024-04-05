package org.breizhcamp.kalon.infrastructure

import org.breizhcamp.kalon.application.dto.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
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
        val member = baseMemberDB.get().toMember()
        member.participations = participations.map { it.toParticipation() }.toSet()

        return Optional.of(member)
    }

    override fun add(creationReq: MemberCreationReq): Member =
        memberRepo.createMember(creationReq.lastname, creationReq.firstname).toMember()

    override fun update(id: UUID, member: MemberPartial): Member {
        memberRepo.updatePartial(id, member.lastname, member.firstname, member.profilePictureLink)
        return getById(id).get()
    }

    override fun addContact(id: UUID, platform: String, link: String): Member {
        memberRepo.addContact(id, platform, link)
        return getById(id).get()
    }

    override fun existsById(id: UUID): Boolean =
        memberRepo.existsById(id)

}
