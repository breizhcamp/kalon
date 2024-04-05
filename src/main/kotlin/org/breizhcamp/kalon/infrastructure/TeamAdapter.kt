package org.breizhcamp.kalon.infrastructure

import org.breizhcamp.kalon.application.dto.TeamCreationReq
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.domain.use_cases.ports.TeamPort
import org.breizhcamp.kalon.infrastructure.db.mappers.toParticipation
import org.breizhcamp.kalon.infrastructure.db.mappers.toTeam
import org.breizhcamp.kalon.infrastructure.db.repos.TeamRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class TeamAdapter (
    private val teamRepo: TeamRepo
): TeamPort {
    override fun list(filter: TeamFilter): List<Team> =
        teamRepo.filter(filter).map { it.toTeam() }

    override fun getById(id: UUID): Optional<Team> {
        val baseTeamDB = teamRepo.findById(id)

        if (baseTeamDB.isEmpty) return Optional.empty()

        val participations = teamRepo.getParticipations(id)
        val team = baseTeamDB.get().toTeam()
        team.participations = participations.map { it.toParticipation() }.toSet()

        return Optional.of(team)
    }

    override fun add(creationReq: TeamCreationReq): Team =
        teamRepo.savePartial(creationReq.name).toTeam()

    override fun update(id: UUID, team: TeamPartial): Team {
        teamRepo.updatePartial(id, team.name, team.description)
        return getById(id).get()
    }

    override fun existsById(id: UUID): Boolean =
        teamRepo.existsById(id)

}