package org.breizhcamp.kalon.application.dto

import java.util.*

data class MemberDTO(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val contacts: List<ContactDTO>,
    val profilePictureLink: String?,
    val participations: List<MemberParticipationDTO>,
    val keycloakId: UUID?
)
