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
        if (notFound)
            logger.warn { "Member:${id} not in database, returning a NOT_FOUND response" }
        return notFound
    }

    fun teamNotFound(id: UUID): Boolean {
        val notFound = !teamExists.exists(id)
        if (notFound)
            logger.warn { "Team:${id} not in database, returning a NOT_FOUND response" }
        return notFound
    }

    fun eventNotFound(id: Int): Boolean {
        val notFound = !eventExists.exists(id)
        if (notFound)
            logger.warn { "Event:${id} not in database, returning a NOT_FOUND response" }
        return notFound
    }

    fun participationNotFound(teamId: UUID, memberId: UUID, eventId: Int): Boolean {
        val notFound = !participationExists.exists(teamId, memberId, eventId)
        if (notFound)
            logger.warn { "Participation not in database, returning a NOT_FOUND response" }
        return notFound
    }

}