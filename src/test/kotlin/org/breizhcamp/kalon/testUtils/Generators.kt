package org.breizhcamp.kalon.testUtils

import org.breizhcamp.kalon.domain.entities.Contact
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.Team
import org.breizhcamp.kalon.infrastructure.db.model.ContactDB
import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.breizhcamp.kalon.infrastructure.db.model.TeamDB
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

fun generateRandomHexString(blocks: Int = 1): String {
    val builder = StringBuilder()

    for (i in 1..blocks) builder.append(
        Random.nextInt().toString(16)
    )

    return builder.toString()
}

fun generateRandomLocalDate(year: Int = 2024): LocalDate =
    LocalDateTime.of(
        year,
        Month.of(Random.nextInt(1, 12)),
        Random.nextInt(1, 28),
        Random.nextInt(0, 23),
        Random.nextInt(0, 59),
        Random.nextInt(0, 59)
    ).toLocalDate()

fun generateRandomMember(): Member = Member(
    id = UUID.randomUUID(),
    lastname = generateRandomHexString(),
    firstname = generateRandomHexString(),
    contacts = emptySet(),
    profilePictureLink = null,
    participations = emptySet()
)

fun generateRandomMemberDB(): MemberDB = MemberDB(
    id = UUID.randomUUID(),
    lastname = generateRandomHexString(2),
    firstname = generateRandomHexString(2),
    contacts = emptySet(),
    profilePictureLink = generateRandomHexString()
)
fun generateRandomContact(): Contact = Contact(
    id = UUID.randomUUID(),
    platform = generateRandomHexString(),
    link = generateRandomHexString()
)

fun generateRandomContactDB(memberId: UUID = UUID.randomUUID()): ContactDB =ContactDB(
    id = UUID.randomUUID(),
    memberId = memberId,
    platform = generateRandomHexString(),
    link = generateRandomHexString()
)

fun generateRandomTeam(): Team = Team(
    id = UUID.randomUUID(),
    name = generateRandomHexString(),
    description = generateRandomHexString(4),
    participations = emptySet()
)

fun generateRandomTeamDB(): TeamDB = TeamDB(
    UUID.randomUUID(),
    generateRandomHexString(),
    generateRandomHexString(4)
)

fun generateRandomEvent(): Event = Event(
    id = Random.nextInt().absoluteValue,
    name = generateRandomHexString(2),
    year = 2020 + Random.nextInt(1, 10),
    debutEvent = generateRandomLocalDate(),
    finEvent = generateRandomLocalDate(),
    debutCFP = generateRandomLocalDate(),
    finCFP = generateRandomLocalDate(),
    debutInscription = generateRandomLocalDate(),
    finInscription = generateRandomLocalDate(),
    website = generateRandomHexString(),
    eventParticipants = emptySet()
)

fun generateRandomEventDB(): EventDB = EventDB(
    id = Random.nextInt().absoluteValue,
    name = generateRandomHexString(2),
    year = 2020 + Random.nextInt(1, 10),
    debutEvent = generateRandomLocalDate(),
    finEvent = generateRandomLocalDate(),
    debutCFP = generateRandomLocalDate(),
    finCFP = generateRandomLocalDate(),
    debutInscription = generateRandomLocalDate(),
    finInscription = generateRandomLocalDate(),
    website = generateRandomHexString()
)
