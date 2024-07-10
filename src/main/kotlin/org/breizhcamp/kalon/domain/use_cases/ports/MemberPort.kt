package org.breizhcamp.kalon.domain.use_cases.ports

import org.breizhcamp.kalon.application.requests.ContactCreationReq
import org.breizhcamp.kalon.application.requests.MemberCreationReq
import org.breizhcamp.kalon.domain.entities.Contact
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.MemberPartial
import java.util.*

interface MemberPort {

    fun list(filter: MemberFilter): List<Member>
    fun getById(id: UUID): Optional<Member>
    fun getKeycloakId(id: UUID): UUID?
    fun add(creationReq: MemberCreationReq): Member
    fun update(id: UUID, member: MemberPartial): Member
    fun addContact(id: UUID, req: ContactCreationReq): Member
    fun getPrivateContacts(id: UUID): List<Contact>
    fun removeContact(id: UUID, contactId: UUID): Member
    fun existsById(id: UUID): Boolean
    fun contactExistsById(id: UUID, contactId: UUID): Boolean

}