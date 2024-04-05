package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(ParticipationExists::class)
class ParticipationExistsTest {

    @MockkBean
    private lateinit var participationPort: ParticipationPort

    @Autowired
    private lateinit var participationExists: ParticipationExists

    @Test
    fun `should call port to test existence`() {
        every { participationPort.existsByIds(any(), any(), 1) } returns true
        every { participationPort.existsByIds(any(), any(), 2) } returns false

        assert(participationExists.exists(UUID.randomUUID(), UUID.randomUUID(), 1))
        assert(!participationExists.exists(UUID.randomUUID(), UUID.randomUUID(), 2))

        verify { participationPort.existsByIds(any(), any(), 1) }
        verify { participationPort.existsByIds(any(), any(), 2) }
    }
}