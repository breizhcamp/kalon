package org.breizhcamp.kalon.application.handlers

import mu.KotlinLogging
import org.breizhcamp.kalon.domain.use_cases.EventExists
import org.breizhcamp.kalon.domain.use_cases.MemberExists
import org.breizhcamp.kalon.domain.use_cases.ParticipationExists
import org.breizhcamp.kalon.domain.use_cases.TeamExists
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {  }

@Service
class HandleNotFound(
    private val teamExists: TeamExists,
    private val memberExists: MemberExists,
    private val eventExists: EventExists,
    private val participationExists: ParticipationExists,
) {

    fun memberNotFound(id: UUID): Boolean {
        val notFound = !memberExists.exists(id)
        log(notFound, "Member:$id")
        return notFound
    }

    fun contactNotFound(id: UUID, contactId: UUID): Boolean {
        if (!memberExists.exists(id)) {
            log(true, "Member:$id")
            return true
        }
        val notFound = !memberExists.contactExists(id, contactId)
        log(notFound, "Contact:$contactId for Member:$id")
        return notFound
    }

    fun teamNotFound(id: UUID): Boolean {
        val notFound = !teamExists.exists(id)
        log(notFound, "Team:$id")
        return notFound
    }

    fun eventNotFound(id: Int): Boolean {
        val notFound = !eventExists.exists(id)
        log(notFound, "Event:$id")
        return notFound
    }

    fun participationNotFound(teamId: UUID, memberId: UUID, eventId: Int): Boolean {
        val notFound = !participationExists.exists(teamId, memberId, eventId)
        log(notFound, "Participation")
        return notFound
    }

    private fun log(notFound: Boolean, attribute: String) {
        if (notFound)
            logger.warn { "$attribute not in database, returning a NOT_FOUND response" }
    }

}