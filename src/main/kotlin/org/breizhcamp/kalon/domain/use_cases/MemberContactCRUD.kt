package org.breizhcamp.kalon.domain.use_cases

import org.breizhcamp.kalon.application.requests.ContactCreationReq
import org.breizhcamp.kalon.domain.entities.Contact
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemberContactCRUD(
    private val memberPort: MemberPort
) {

    fun create(id: UUID, req: ContactCreationReq): Member = memberPort.addContact(id, req)
    fun getPrivateMethods(id: UUID): List<Contact> = memberPort.getPrivateContacts(id)
    fun delete(id: UUID, contactId: UUID): Member = memberPort.removeContact(id, contactId)
}