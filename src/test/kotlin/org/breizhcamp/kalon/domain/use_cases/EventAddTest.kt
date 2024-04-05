package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventAdd::class)
class EventAddTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventAdd: EventAdd

    private val testReqNonNullName = EventCreationReq(2024, "Breizh camp 2024")
    private val testEventNonNullName = Event(
        id = 1,
        name = "Breizh camp 2024",
        year = 2024,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        eventParticipants = emptySet(),
        website = null,
    )

    @Test
    fun `should call port to add event`() {
        every {
            eventPort.add(testReqNonNullName)
        } returns testEventNonNullName

        assertEquals(eventAdd.add(testReqNonNullName), testEventNonNullName)

        verify { eventPort.add(testReqNonNullName) }
    }

}