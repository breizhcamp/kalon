package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.*
import java.util.*

@Entity @Table(name = "member")
data class MemberDB(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    val lastname: String,
    val firstname : String,
    @OneToMany(
        targetEntity = ContactDB::class,
        mappedBy = "memberId"
    )
    val contacts: Set<ContactDB>,
    @Column(name = "profile_picture_lnk")
    val profilePictureLink: String?
)
