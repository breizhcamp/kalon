package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TeamRepo: JpaRepository<TeamDB, UUID>, TeamRepoCustom {

    @Query("""
        INSERT INTO team(name) 
        VALUES (?)
        RETURNING *
    """, nativeQuery = true)
    fun savePartial(name: String): TeamDB

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE team
        SET name = 
            CASE WHEN (?2 IS NOT NULL) 
            THEN ?2
            ELSE name
            END, 
        description = 
            CASE WHEN (?3 IS NOT NULL)
            THEN ?3
            ELSE description
            END
        WHERE id = ?1 
    """, nativeQuery = true)
    fun updatePartial(id: UUID, name: String?, description: String?)

}