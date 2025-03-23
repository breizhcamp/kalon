package org.breizhcamp.kalon.infrastructure.db.repos

import com.querydsl.jpa.JPAExpressions
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.infrastructure.db.model.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.util.*

class TeamRepoCustomImpl: QuerydslRepositorySupport(TeamDB::class.java), TeamRepoCustom {

    override fun filter(filter: TeamFilter): List<TeamDB> {
        val team = QTeamDB.teamDB
        val participation = QParticipationDB.participationDB
        val query = from(team)

        filter.eventId?.let {
            query.where(team.id.`in`(
                JPAExpressions.select(participation.participationDBId.teamId)
                    .from(participation)
                    .where(participation.participationDBId.eventId.eq(it))
            ))
        }

        filter.memberId?.let {
            query.where(team.id.`in`(
                JPAExpressions.select(participation.participationDBId.memberId)
                    .from(participation)
                    .where(participation.participationDBId.memberId.eq(it))
            ))
        }

        return query.fetch()
    }

    override fun getParticipations(id: UUID): Set<TeamParticipationDB> {
        val participation = QParticipationDB.participationDB
        val event = QEventDB.eventDB
        val member = QMemberDB.memberDB

        val participations = from(participation)
            .where(participation.participationDBId.teamId.eq(id))
            .fetch()

        return participations.map { TeamParticipationDB(
            eventDB = from(event).where(event.id.eq(it.participationDBId.eventId)).fetch().first(),
            memberDB = from(member).where(member.id.eq(it.participationDBId.memberId)).fetch().first(),
        ) }.toSet()
    }

}