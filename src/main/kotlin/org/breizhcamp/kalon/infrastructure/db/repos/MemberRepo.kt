package org.breizhcamp.kalon.infrastructure.db.repos

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
        INSERT INTO contact(member_id, platform, link) 
        VALUES (?, ?, ?)
    """, nativeQuery = true)
    fun addContact(memberId: UUID, platform: String, link: String)

    @Query("""
        INSERT INTO member(lastname, firstname) 
        VALUES (?, ?)
        RETURNING id, lastname, firstname, profile_picture_lnk
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

}