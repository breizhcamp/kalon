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
import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import org.breizhcamp.kalon.infrastructure.db.repos.TeamRepo
import org.breizhcamp.kalon.testUtils.generateRandomHexString
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
        val returned = listOf(0, 1, 2).map(this::testTeamDB)

        every { teamRepo.filter(filter) } returns returned

        assertEquals(teamAdapter.list(filter), returned.map { it.toTeam() })

        verify { teamRepo.filter(filter) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `getById should always call repo!findById, call repo!getParticipations when result not empty, and return Optional of Team`(
        exists: Boolean
    ) {
        val id = UUID.randomUUID()
        val option = if (exists) Optional.of(testTeamDB().copy(id = id)) else Optional.empty()

        every { teamRepo.findById(id) } returns option
        every { teamRepo.getParticipations(id) } returns emptySet()

        val result = teamAdapter.getById(id)

        verify { teamRepo.findById(id) }

        if(exists) {
            assertEquals(result, Optional.of(testTeamDB().copy(id = id).toTeam()))

            verify { teamRepo.getParticipations(id) }
        } else {
            val emptyOptional: Optional<Team> = Optional.empty()
            assertEquals(result, emptyOptional)

            verify { teamRepo.getParticipations(id) wasNot Called }
        }
    }

    @Test
    fun `add should call repo with name in request and return the Team`() {
        val name = generateRandomHexString(2)

        val request = TeamCreationReq(name)
        val teamDB = testTeamDB().copy(name = name)

        every { teamRepo.savePartial(name) } returns teamDB

        assertEquals(teamAdapter.add(request), teamDB.toTeam())

        verify { teamRepo.savePartial(name) }
    }

    @Test
    fun `update should transmit id and exploded TeamPartial to repo and return the Team`() {
        val id = UUID.randomUUID()
        val name = generateRandomHexString()
        val description = generateRandomHexString(4)

        val partial = TeamPartial(
            name = name,
            description = description
        )
        val teamDB = TeamDB(
            id = id,
            name = name,
            description = description
        )

        every { teamRepo.updatePartial(
            id = id,
            name = name,
            description = description
        ) } returns Unit
        every { teamRepo.findById(id) } returns Optional.of(teamDB)
        every { teamRepo.getParticipations(id) } returns emptySet()

        assertEquals(teamAdapter.update(id, partial), teamDB.toTeam())

        verify { teamRepo.updatePartial(
            id = id,
            name = name,
            description = description
        ) }
        verify { teamRepo.findById(id) }
        verify { teamRepo.getParticipations(id) }
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

    private fun testTeamDB(index: Int = 0): TeamDB {
        val names = listOf("Orga", "Bureau", "Comm")

        val i = if (index > 2) 0 else index

        return TeamDB(
            id = UUID.randomUUID(),
            name = names[i],
            description = null
        )
    }

}