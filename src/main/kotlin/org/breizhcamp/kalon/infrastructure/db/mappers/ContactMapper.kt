package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.Contact
import org.breizhcamp.kalon.infrastructure.db.model.ContactDB
import java.util.UUID

fun ContactDB.toContact() = Contact(
    id = id,
    platform = platform,
    link = link,
)