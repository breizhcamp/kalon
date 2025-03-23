package org.breizhcamp.kalon.infrastructure.db.repos

import com.querydsl.jpa.JPAExpressions
import mu.KotlinLogging
import org.breizhcamp.kalon.domain.entities.MemberFilter
import org.breizhcamp.kalon.domain.entities.Order
import org.breizhcamp.kalon.infrastructure.db.model.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.util.*

private val logger = KotlinLogging.logger {  }

class MemberRepoCustomImpl: QuerydslRepositorySupport(MemberDB::class.java), MemberRepoCustom {

    override fun filter(filter: MemberFilter): List<MemberDB> {
        val member = QMemberDB.memberDB
        val participation = QParticipationDB.participationDB
        val query = from(member)

        filter.teamId?.let {
            query.where(member.id.`in`(
                JPAExpressions.select(participation.participationDBId.memberId)
                    .from(participation)
                    .where(participation.participationDBId.teamId.eq(it))
            ))
        }

        filter.eventId?.let {
            query.where(member.id.`in`(
                JPAExpressions.select(participation.participationDBId.memberId)
                    .from(participation)
                    .where(participation.participationDBId.eventId.eq(it))
            ))
        }

        val nameOrder = member.lastname.concat(member.firstname)
        when (filter.nameOrder) {
            Order.ASC -> query.orderBy(nameOrder.asc())
            Order.DESC -> query.orderBy(nameOrder.desc())
            else -> {
                logger.info { "No order provided, defaulting to ASC" }
                query.orderBy(nameOrder.asc())
            }
        }

        filter.limit?.let {
            query.limit(it)
        }

        return query.fetch()
    }

    override fun getParticipations(id: UUID): Set<MemberParticipationDB> {
        val participation = QParticipationDB.participationDB
        val event = QEventDB.eventDB
        val team = QTeamDB.teamDB

        val participations = from(participation)
            .where(participation.participationDBId.memberId.eq(id))
            .fetch()

        return participations.map { MemberParticipationDB(
            eventDB = from(event).where(event.id.eq(it.participationDBId.eventId)).fetch().first(),
            teamDB = from(team).where(team.id.eq(it.participationDBId.teamId)).fetch().first()
        ) }.toSet()
    }

}