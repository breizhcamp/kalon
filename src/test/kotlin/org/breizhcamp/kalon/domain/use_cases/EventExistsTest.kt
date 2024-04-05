package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventExists::class)
class EventExistsTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventExists: EventExists

    @Test
    fun `should call port to test existence`() {
        every { eventPort.exists(1) } returns true
        every { eventPort.exists(2) } returns false

        assert(eventExists.exists(1))
        assert(!eventExists.exists(2))

        verify { eventPort.exists(1) }
        verify { eventPort.exists(2) }
    }

}