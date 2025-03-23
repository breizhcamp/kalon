package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.junit.jupiter.api.Assertions.assertEquals
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
@WebMvcTest(ParticipationExists::class)
class ParticipationExistsTest {

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var participationExists: ParticipationExists

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should call port to test existence`(exists: Boolean) {
        every { participationPort.existsByIds(any(), any(), any()) } returns exists

        assertEquals(participationExists.exists(UUID.randomUUID(), UUID.randomUUID(), Random.nextInt().absoluteValue), exists)

        verify { participationPort.existsByIds(any(), any(), any()) }
    }
}