package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MemberExists::class)
class MemberExistsTest {

    @MockkBean
    private lateinit var memberPort: MemberPort

    @Autowired
    private lateinit var memberExists: MemberExists

    private val existsUUID = UUID.randomUUID()
    private val doesNotExistsUUID = UUID.randomUUID()

    @Test
    fun `should call port to test existence`() {
        every { memberPort.existsById(existsUUID) } returns true
        every { memberPort.existsById(doesNotExistsUUID) } returns false

        assert(memberExists.exists(existsUUID))
        assert(!memberExists.exists(doesNotExistsUUID))

        verify { memberPort.existsById(existsUUID) }
        verify { memberPort.existsById(doesNotExistsUUID) }
    }
}