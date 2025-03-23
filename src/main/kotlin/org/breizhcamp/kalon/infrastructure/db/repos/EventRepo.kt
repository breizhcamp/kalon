package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

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
        debut_event =       CASE WHEN (CAST (?4 AS DATE) IS NOT NULL)  THEN CAST(?4 AS DATE) ELSE debut_event END,
        fin_event =         CASE WHEN (CAST (?5 AS DATE) IS NOT NULL)  THEN CAST(?5 AS DATE) ELSE fin_event END,
        debut_cfp =         CASE WHEN (CAST (?6 AS DATE) IS NOT NULL)  THEN CAST(?6 AS DATE) ELSE debut_cfp END,
        fin_cfp =           CASE WHEN (CAST (?7 AS DATE) IS NOT NULL)  THEN CAST(?7 AS DATE) ELSE fin_cfp END,
        debut_inscription = CASE WHEN (CAST (?8 AS DATE) IS NOT NULL)  THEN CAST(?8 AS DATE) ELSE debut_inscription END,
        fin_inscription =   CASE WHEN (CAST (?9 AS DATE) IS NOT NULL)  THEN CAST(?9 AS DATE) ELSE fin_inscription END,
        website =           CASE WHEN (?10 IS NOT NULL) THEN ?10    ELSE website END
        WHERE id = ?1 
    """, nativeQuery = true)
    fun updateInfos(
        id: Int,
        name: String?,
        year: Int?,
        debutEvent: LocalDate?,
        finEvent: LocalDate?,
        debutCFP: LocalDate?,
        finCFP: LocalDate?,
        debutInscription: LocalDate?,
        finInscription: LocalDate?,
        website: String?,
    )
}