package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.EventPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.math.absoluteValue
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@WebMvcTest(EventExists::class)
class EventExistsTest {

    @MockkBean
    private lateinit var eventPort: EventPort

    @Autowired
    private lateinit var eventExists: EventExists

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should call port to test existence`(exists: Boolean) {
        val id = Random.nextInt().absoluteValue

        every { eventPort.exists(id) } returns exists

        assertEquals(eventExists.exists(id), exists)

        verify { eventPort.exists(id) }
    }

}