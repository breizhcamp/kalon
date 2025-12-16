package org.breizhcamp.kalon.domain.use_cases

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.ports.EventPort
import org.breizhcamp.kalon.helpers.EventHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class EventListTest {

    @RelaxedMockK
    private lateinit var eventPort: EventPort

    @InjectMockKs
    private lateinit var eventCRUD: EventCRUD

    @Test
    fun `should return empty list when no events`() {
        /**** GIVEN ****/
        every { eventPort.list() }.returns(emptyList())

        /**** WHEN ****/
        val events = eventCRUD.list()

        /**** THEN ****/
        assertThat(events).isEmpty()
    }

    @Test
    fun `should return all events`() {
        /**** GIVEN ****/
        val now = LocalDate.now()
        val event1 = EventHelper.get(
            id = "event1",
            name = "event1",
            startDate = now.plusDays(10),
            endDate = now.plusDays(12)
        )

        val event2 = EventHelper.get(
            id = "event2",
            name = "event2",
            startDate = now.plusDays(5),
            endDate = now.plusDays(7)
        )

        val event3 = EventHelper.get(
            id = "event3",
            name = "event3",
            startDate = now.plusDays(20),
            endDate = now.plusDays(22)
        )

        every { eventPort.list() }.returns(listOf(event3, event1, event2))

        /**** WHEN ****/
        val events = eventCRUD.list()

        /**** THEN ****/
        assertThat(events).hasSize(3)
        // Should be kept the order from the port
        assertThat(events[0].id).isEqualTo(EventId("event3"))
        assertThat(events[1].id).isEqualTo(EventId("event1"))
        assertThat(events[2].id).isEqualTo(EventId("event2"))
    }
}

