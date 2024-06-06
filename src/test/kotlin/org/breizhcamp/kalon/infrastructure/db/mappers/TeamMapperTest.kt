package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.testUtils.generateRandomTeamDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TeamMapperTest {

    @Test
    fun `toTeam should transmit values and create an empty set for participations`() {
        val teamDB = generateRandomTeamDB()
        val team = teamDB.toTeam()
        val emptyParticipationSet: Set<TeamParticipation> = emptySet()

        assertEquals(team.id, teamDB.id)
        assertEquals(team.name, teamDB.name)
        assertEquals(team.description, teamDB.description)
        assertEquals(team.participations, emptyParticipationSet)
    }
}