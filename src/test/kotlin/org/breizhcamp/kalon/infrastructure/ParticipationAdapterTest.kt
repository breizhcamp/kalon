package org.breizhcamp.kalon.infrastructure

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDB
import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDBId
import org.breizhcamp.kalon.infrastructure.db.repos.ParticipationRepo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(ParticipationAdapter::class)
class ParticipationAdapterTest {

    @MockkBean
    private lateinit var participationRepo: ParticipationRepo

    @Autowired
    private lateinit var participationAdapter: ParticipationAdapter

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun existsByIds(exists: Boolean) {
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        every { participationRepo.existsById(ParticipationDBId(memberId, teamId, eventId)) } returns exists

        assertEquals(participationAdapter.existsByIds(teamId, memberId, eventId), exists)

        verify { participationRepo.existsById(ParticipationDBId(memberId, teamId, eventId)) }
    }

    @Test
    fun `createByIds should call repo with composite ids in a ParticipationDB`() {
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        val participationDB = ParticipationDB(ParticipationDBId(
            memberId, teamId, eventId
        ))

        every { participationRepo.save(participationDB) } returns participationDB

        participationAdapter.createByIds(teamId, memberId, eventId)

        verify { participationRepo.save(participationDB) }
    }

    @Test
    fun `removeByIds should call repo with composited key`() {
        val teamId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        val eventId = Random.nextInt().absoluteValue

        val participationId = ParticipationDBId(
            memberId, teamId, eventId
        )

        every { participationRepo.deleteById(participationId) } returns Unit

        participationAdapter.removeByIds(teamId, memberId, eventId)

        verify { participationRepo.deleteById(participationId) }
    }
}