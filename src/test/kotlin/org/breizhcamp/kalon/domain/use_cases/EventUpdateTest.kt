package org.breizhcamp.kalon.domain.use_cases

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.breizhcamp.kalon.domain.exceptions.InconsistentStartEndDateException
import org.breizhcamp.kalon.domain.exceptions.NotFoundException
import org.breizhcamp.kalon.domain.ports.EventPort
import org.breizhcamp.kalon.helpers.EventHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class EventUpdateTest {

    @RelaxedMockK
    private lateinit var eventPort: EventPort

    @InjectMockKs
    private lateinit var eventCRUD: EventCRUD

    @Test
    fun `should throw exception if id does not exist`() {
        /**** GIVEN ****/
        every { eventPort.isIdExists(any()) }.returns(false)
        val event = EventHelper.get()

        /**** WHEN / THEN ****/
        assertThrows<NotFoundException> { eventCRUD.update(event) }
    }

    @Test
    fun `should throw exception if start date is after end date`() {
        /**** GIVEN ****/
        every { eventPort.isIdExists(any()) }.returns(true)
        val now = LocalDate.now()
        val event = EventHelper.get(
            startDate = now.plusDays(1),
            endDate = now
        )

        /**** WHEN / THEN ****/
        assertThrows<InconsistentStartEndDateException> {
            eventCRUD.update(event)
        }
    }

    @Test
    fun `should update a new event`() {
        /**** GIVEN ****/
        every { eventPort.isIdExists(any()) }.returns(true)
        val event = EventHelper.get()

        /**** WHEN ****/
        eventCRUD.update(event)

        /**** THEN ****/
        verify { eventPort.update(event) }
    }
}
