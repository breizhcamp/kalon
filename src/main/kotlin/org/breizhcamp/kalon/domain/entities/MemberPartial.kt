package org.breizhcamp.kalon.domain.entities

data class MemberPartial(
    val lastname: String?,
    val firstname: String?,
    val profilePictureLink: String?,
) {
    companion object{
        fun empty() = MemberPartial(null, null, null)
    }
}
