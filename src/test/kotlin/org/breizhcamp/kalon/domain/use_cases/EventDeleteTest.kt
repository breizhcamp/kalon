package org.breizhcamp.kalon.domain.use_cases

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.breizhcamp.kalon.domain.exceptions.NotFoundException
import org.breizhcamp.kalon.domain.ports.EventPort
import org.breizhcamp.kalon.helpers.EventHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class EventDeleteTest {

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
        assertThrows<NotFoundException> { eventCRUD.delete(event.id) }
    }

    @Test
    fun `should delete the event`() {
        /**** GIVEN ****/
        every { eventPort.isIdExists(any()) }.returns(true)
        val event = EventHelper.get()

        /**** WHEN ****/
        eventCRUD.delete(event.id)

        /**** THEN ****/
        verify { eventPort.delete(event.id) }
    }
}
