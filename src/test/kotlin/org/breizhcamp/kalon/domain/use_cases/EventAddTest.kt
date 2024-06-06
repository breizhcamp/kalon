package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.breizhcamp.kalon.testUtils.generateRandomHexString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAdd::class)
class EventAddTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventAdd: EventAdd

    @Test
    fun `should call port to add event`() {
        val req = EventCreationReq(Random.nextInt().absoluteValue, generateRandomHexString(2))
        val event = generateRandomEvent().copy(year = req.year, name = req.name)

        every {
            eventPort.add(req)
        } returns event

        assertEquals(eventAdd.add(req), event)

        verify { eventPort.add(req) }
    }

}