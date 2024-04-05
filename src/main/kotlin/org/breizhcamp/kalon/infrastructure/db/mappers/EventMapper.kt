package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventParticipant
import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.breizhcamp.kalon.infrastructure.db.model.EventParticipantDB

fun EventDB.toEvent() = Event(
    id = id,
    year = year,
    name = name,
    debutEvent = debutEvent,
    finEvent = finEvent,
    debutCFP = debutCFP,
    finCFP = finCFP,
    debutInscription = debutInscription,
    finInscription = finInscription,
    website = website,
    eventParticipants = emptySet()
)

fun EventParticipantDB.toParticipant() = EventParticipant(
    member = memberDB.toMember(),
    team = teamDB.toTeam(),
)