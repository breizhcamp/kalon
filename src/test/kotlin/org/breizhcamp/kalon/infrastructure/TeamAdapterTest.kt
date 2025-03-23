package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.TeamCreationReq
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.domain.entities.TeamFilter
import org.breizhcamp.kalon.domain.entities.TeamPartial
import org.breizhcamp.kalon.infrastructure.db.mappers.toTeam
import org.breizhcamp.kalon.infrastructure.db.repos.TeamRepo
import org.breizhcamp.kalon.testUtils.generateRandomTeamDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TeamAdapter::class)
class TeamAdapterTest {

    @MockkBean
    private lateinit var teamRepo: TeamRepo

    @Autowired
    private lateinit var teamAdapter: TeamAdapter

    @Test
    fun `list should call repo with provided filter and return a list of Teams`() {
        val filter = TeamFilter.empty()
        val returned = listOf(
            generateRandomTeamDB(),
            generateRandomTeamDB(),
            generateRandomTeamDB()
        )

        every { teamRepo.filter(filter) } returns returned

        assertEquals(teamAdapter.list(filter), returned.map { it.toTeam() })

        verify { teamRepo.filter(filter) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipations when result not empty, and return Optional of Team`(
        exists: Boolean
    ) {
        val team = generateRandomTeamDB()
        val option = if (exists) Optional.of(team) else Optional.empty()

        every { teamRepo.findById(team.id) } returns option
        every { teamRepo.getParticipations(team.id) } returns emptySet()

        val result = teamAdapter.getById(team.id)

        verify { teamRepo.findById(team.id) }

        if(exists) {
            assertEquals(result, Optional.of(team.toTeam()))

            verify { teamRepo.getParticipations(team.id) }
        } else {
            val emptyOptional: Optional<Team> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { teamRepo.getParticipations(team.id) wasNot Called }
        }
    }

    @Test
    fun `add should call repo with name in request and return the Team`() {
        val teamDB = generateRandomTeamDB()
        val request = TeamCreationReq(teamDB.name)

        every { teamRepo.savePartial(request.name) } returns teamDB

        assertEquals(teamAdapter.add(request), teamDB.toTeam())

        verify { teamRepo.savePartial(request.name) }
    }

    @Test
    fun `update should transmit id and exploded TeamPartial to repo and return the Team`() {
        val teamDB = generateRandomTeamDB()
        val partial = TeamPartial(name = teamDB.name, description = teamDB.description)

        every { teamRepo.updatePartial(
            id = teamDB.id,
            name = partial.name,
            description = partial.description
        ) } returns Unit
        every { teamRepo.findById(teamDB.id) } returns Optional.of(teamDB)
        every { teamRepo.getParticipations(teamDB.id) } returns emptySet()

        assertEquals(teamAdapter.update(teamDB.id, partial), teamDB.toTeam())

        verify { teamRepo.updatePartial(
            id = teamDB.id,
            name = partial.name,
            description = partial.description
        ) }
        verify { teamRepo.findById(teamDB.id) }
        verify { teamRepo.getParticipations(teamDB.id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `existsById should call repo and return a Boolean`(
        exists: Boolean
    ) {
        val id = UUID.randomUUID()
        every { teamRepo.existsById(id) } returns exists

        assertEquals(teamAdapter.existsById(id), exists)

        verify { teamRepo.existsById(id) }
    }

}