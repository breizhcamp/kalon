package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.breizhcamp.kalon.testUtils.generateRandomHexString
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

    @Test
    fun `should call port to update infos`() {
        val eventBefore = generateRandomEvent()
        val partial = EventPartial.empty().copy(name = generateRandomHexString())
        val eventAfter = eventBefore.copy(name = partial.name)
        every { eventPort.updateInfos(eventBefore.id, partial) } returns eventAfter

        assertEquals(eventUpdateInfos.updateInfos(eventBefore.id, partial), eventAfter)

        verify { eventPort.updateInfos(eventBefore.id, partial) }
    }
}