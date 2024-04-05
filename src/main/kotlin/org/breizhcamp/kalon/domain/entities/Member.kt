package org.breizhcamp.kalon.domain.entities

import java.util.*

data class Member(
    val id: UUID,
    val lastname: String,
    val firstname: String,
    val contacts: Set<Contact>,
    val profilePictureLink: String?,
    var participations: Set<MemberParticipation>,
)
