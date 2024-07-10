package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.ContactDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepo: JpaRepository<MemberDB, UUID>, MemberRepoCustom {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        INSERT INTO contact(member_id, platform, link, public) 
        VALUES (?, ?, ?, ?)
    """, nativeQuery = true)
    fun addContact(memberId: UUID, platform: String, link: String, public: Boolean)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM ContactDB c WHERE c.id = ?1
    """)
    fun removeContact(contactId: UUID)

    @Query("""
        SELECT COUNT(*) != 0 FROM ContactDB contact WHERE contact.memberId = ?1 AND contact.id = ?2
    """)
    fun existsContactByIds(id: UUID, contactId: UUID): Boolean

    @Query("""
        INSERT INTO member(lastname, firstname) 
        VALUES (?, ?)
        RETURNING member.*
    """, nativeQuery = true)
    fun createMember(lastname: String, firstname: String): MemberDB

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE member
        SET lastname = 
            CASE WHEN (?2 IS NOT NULL) 
            THEN ?2
            ELSE lastname
            END, 
        firstname = 
            CASE WHEN (?3 IS NOT NULL)
            THEN ?3
            ELSE firstname
            END,
        profile_picture_lnk =
            CASE WHEN (?4 IS NOT NULL)
            THEN ?4
            ELSE profile_picture_lnk
            END
        WHERE id = ?1 
    """, nativeQuery = true)
    fun updatePartial(id: UUID, lastname: String?, firstname: String?, profilePictureLink: String?)

    @Query("""
        SELECT contact FROM ContactDB contact WHERE contact.memberId = ?1 AND contact.public
    """)
    fun getPublicContacts(id: UUID): Set<ContactDB>

    @Query("""
        SELECT member.keycloakId FROM MemberDB member WHERE member.id = ?1
    """)
    fun getKeycloakIdById(id: UUID): UUID?

    @Query("""
        SELECT contact FROM ContactDB contact WHERE contact.memberId = ?1 AND NOT contact.public
    """)
    fun getPrivateContacts(id: UUID): Set<ContactDB>

}