package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventUpdateInfos::class)
class EventUpdateInfosTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventUpdateInfos: EventUpdateInfos

    private val testEventBefore = Event(
        id = 1,
        name = null,
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

    private val updateValues = EventPartial(
        name = "Breizh camp 2024",
        year = null,
        debutEvent = null,
        finEvent = null,
        debutCFP = null,
        finCFP = null,
        debutInscription = null,
        finInscription = null,
        website = "example.com"
    )

    private val testEventAfter = testEventBefore.copy(
        name = "Breizh camp 2024",
        website = "example.com"
    )

    @Test
    fun `should call port to update infos`() {
        every { eventPort.updateInfos(testEventBefore.id, updateValues) } returns testEventAfter

        assertEquals(eventUpdateInfos.updateInfos(testEventBefore.id, updateValues), testEventAfter)

        verify { eventPort.updateInfos(testEventBefore.id, updateValues) }
    }
}