package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.TeamParticipation
import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TeamMapperTest {

    @Test
    fun `toTeam should transmit values and create an empty set for participations`() {
        val id = UUID.randomUUID()
        val name = generateRandomHexString()
        val description = generateRandomHexString(3)

        val teamDB = TeamDB(id, name, description)
        val team = teamDB.toTeam()
        val emptyParticipationSet: Set<TeamParticipation> = emptySet()

        assertEquals(team.id, teamDB.id)
        assertEquals(team.name, teamDB.name)
        assertEquals(team.description, teamDB.description)
        assertEquals(team.participations, emptyParticipationSet)
    }
}