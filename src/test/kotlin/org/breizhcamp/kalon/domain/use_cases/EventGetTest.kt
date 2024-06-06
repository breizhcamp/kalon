package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventGet::class)
class EventGetTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventGet: EventGet

    @Test
    fun `should call port to get event`() {
        every {
            eventPort.getById(1)
        } returns Optional.of(generateRandomEvent())

        assert(eventGet.get(1).isPresent)

        verify { eventPort.getById(1) }
    }
}