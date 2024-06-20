package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.kalon.application.dto.EventCreationReq
import org.breizhcamp.kalon.domain.entities.EventFilter
import org.breizhcamp.kalon.domain.entities.EventPartial
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.breizhcamp.kalon.testUtils.generateRandomEvent
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
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventCrud::class)
class EventCrudTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventCrud: EventCrud

    @Test
    fun `create should call port`() {
        val req = EventCreationReq(Random.nextInt().absoluteValue, generateRandomHexString(2))
        val event = generateRandomEvent().copy(year = req.year, name = req.name)

        every {
            eventPort.add(req)
        } returns event

        assertEquals(eventCrud.create(req), event)

        verify { eventPort.add(req) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `exists should call port`(exists: Boolean) {
        val id = Random.nextInt().absoluteValue

        every { eventPort.exists(id) } returns exists

        assertEquals(eventCrud.exists(id), exists)

        verify { eventPort.exists(id) }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `get should call port`(exists: Boolean) {
        val event = generateRandomEvent()
        every {
            eventPort.getById(event.id)
        } returns if (exists) Optional.of(event) else Optional.empty()

        val result = eventCrud.get(event.id)

        assertEquals(exists, result.isPresent)
        if (exists) {
            assertEquals(event, result.get())
        }

        verify { eventPort.getById(event.id) }
    }

    @Test
    fun `list should call port`() {
        val event = generateRandomEvent()
        every { eventPort.list(EventFilter.default()) } returns listOf(event)

        assert(eventCrud.list(EventFilter.default()).contains(event))

        verify { eventPort.list(EventFilter.default()) }
    }

    @Test
    fun `update should call port`() {
        val eventBefore = generateRandomEvent()
        val partial = EventPartial.empty().copy(name = generateRandomHexString())
        val eventAfter = eventBefore.copy(name = partial.name)
        every { eventPort.update(eventBefore.id, partial) } returns eventAfter

        assertEquals(eventCrud.update(eventBefore.id, partial), eventAfter)

        verify { eventPort.update(eventBefore.id, partial) }
    }

    @Test
    fun `delete should call port`() {
        val id = Random.nextInt().absoluteValue
        every { eventPort.delete(id) } just Runs

        eventCrud.delete(id)

        verify { eventPort.delete(id) }
    }
}