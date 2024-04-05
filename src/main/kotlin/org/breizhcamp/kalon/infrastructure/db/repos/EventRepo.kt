package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepo: JpaRepository<EventDB, Int>, EventRepoCustom {

    @Query("""
        INSERT INTO event(name, year)
        VALUES (?, ?)
        RETURNING *
    """, nativeQuery = true)
    fun createEvent(name: String?, year: Int): EventDB

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE event
        SET name =          CASE WHEN (?2 IS NOT NULL)  THEN ?2     ELSE name END, 
        year =              CASE WHEN (?3 IS NOT NULL)  THEN ?3     ELSE year END,
        debut_event =       CASE WHEN (?4 IS NOT NULL)  THEN ?4     ELSE debut_event END,
        fin_event =         CASE WHEN (?5 IS NOT NULL)  THEN ?5     ELSE fin_event END,
        debut_cfp =         CASE WHEN (?6 IS NOT NULL)  THEN ?6     ELSE debut_cfp END,
        fin_cfp =           CASE WHEN (?7 IS NOT NULL)  THEN ?7     ELSE fin_cfp END,
        debut_inscription = CASE WHEN (?8 IS NOT NULL)  THEN ?8     ELSE debut_inscription END,
        fin_inscription =   CASE WHEN (?9 IS NOT NULL)  THEN ?9     ELSE fin_inscription END,
        website =           CASE WHEN (?10 IS NOT NULL) THEN ?10    ELSE website END
        WHERE id = ?1 
    """, nativeQuery = true)
    fun updateInfos(
        id: Int,
        name: String?,
        year: Int?,
        debutEvent: LocalDateTime?,
        finEvent: LocalDateTime?,
        debutCFP: LocalDateTime?,
        finCFP: LocalDateTime?,
        debutInscription: LocalDateTime?,
        finInscription: LocalDateTime?,
        website: String?,
    )
}