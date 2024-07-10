package org.breizhcamp.kalon.infrastructure.db.mappers

import org.breizhcamp.kalon.domain.entities.Member
import org.breizhcamp.kalon.domain.entities.MemberParticipation
import org.breizhcamp.kalon.infrastructure.db.model.MemberDB
import org.breizhcamp.kalon.infrastructure.db.model.MemberParticipationDB

fun MemberDB.toMember() = Member(
    id = id,
    lastname = lastname,
    firstname = firstname,
    contacts = contacts.map { it.toContact() }.toSet(),
    profilePictureLink = profilePictureLink,
    participations = emptySet(),
    keycloakId = keycloakId
)

fun MemberParticipationDB.toParticipation() = MemberParticipation(
    team = teamDB.toTeam(),
    event = eventDB.toEvent(),
)
