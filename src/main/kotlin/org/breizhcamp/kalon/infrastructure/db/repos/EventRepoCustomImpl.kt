package org.breizhcamp.kalon.infrastructure.db.repos

import com.querydsl.jpa.JPAExpressions
import mu.KotlinLogging
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.Order
import org.breizhcamp.kalon.infrastructure.db.model.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

private val logger = KotlinLogging.logger {  }

class EventRepoCustomImpl: QuerydslRepositorySupport(EventDB::class.java), EventRepoCustom {

    override fun filter(filter: EventFilter): List<EventDB> {
        val event = QEventDB.eventDB
        val participation = QParticipationDB.participationDB
        val query = from(event)

        filter.hasParticipantMemberId?.let {
            query.where(event.id.`in`(
                JPAExpressions.select(participation.participationDBId.eventId)
                    .from(participation)
                    .where(participation.participationDBId.memberId.eq(it))
            ))
        }

        filter.hasParticipatingTeamId?.let {
            query.where(event.id.`in`(
                JPAExpressions.select(participation.participationDBId.eventId)
                    .from(participation)
                    .where(participation.participationDBId.teamId.eq(it))
            ))
        }

        filter.yearAfter?.let {
            query.where(event.year.goe(it))
        }

        filter.yearBefore?.let {
            query.where(event.year.loe(it))
        }

        when (filter.yearOrder) {
            Order.ASC -> query.orderBy(event.year.asc())
            Order.DESC -> query.orderBy(event.year.desc())
            else -> {
                logger.info { "No order provided, defaulting to DESC" }
                query.orderBy(event.year.desc())
            }
        }

        filter.limit?.let {
            query.limit(it)
        }

        return query.fetch()
    }

    override fun getParticipants(id: Int): Set<EventParticipantDB> {
        val participation = QParticipationDB.participationDB
        val member = QMemberDB.memberDB
        val team = QTeamDB.teamDB

        val participants = from(participation)
            .where(participation.participationDBId.eventId.eq(id))
            .fetch()

        return participants.map { EventParticipantDB(
            memberDB = from(member).where(member.id.eq(it.participationDBId.memberId)).fetch().first(),
            teamDB = from(team).where(team.id.eq(it.participationDBId.teamId)).fetch().first()
        ) }.toSet()
    }

}