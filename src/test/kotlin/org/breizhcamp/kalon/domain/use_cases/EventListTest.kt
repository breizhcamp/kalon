package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventList::class)
class EventListTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventList: EventList

    @Test
    fun `should call port to list events`() {
        val event = generateRandomEvent()
        every { eventPort.list(EventFilter.default()) } returns listOf(event)

        assert(eventList.list(EventFilter.default()).contains(event))

        verify { eventPort.list(EventFilter.default()) }
    }
}