package org.breizhcamp.kalon.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.breizhcamp.kalon.domain.use_cases.ports.MemberPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `should call port to test existence`(exists: Boolean) {
        val id = UUID.randomUUID()
        every { memberPort.existsById(id) } returns exists

        assertEquals(memberExists.exists(id), exists)

        verify { memberPort.existsById(id) }
    }
}