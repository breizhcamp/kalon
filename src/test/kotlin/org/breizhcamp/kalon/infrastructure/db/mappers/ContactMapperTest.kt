package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.testUtils.generateRandomContactDB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ContactMapperTest {

    @Test
    fun `toContact should transmit all values`() {
        val contactDB = generateRandomContactDB(UUID.randomUUID())
        val contact = contactDB.toContact()

        assertEquals(contact.id, contactDB.id)
        assertEquals(contact.platform, contactDB.platform)
        assertEquals(contact.link, contactDB.link)
    }
}