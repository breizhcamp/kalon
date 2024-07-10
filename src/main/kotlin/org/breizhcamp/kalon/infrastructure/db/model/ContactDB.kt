package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "contact")
data class ContactDB(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Column(name = "member_id")
    val memberId: UUID,
    val platform: String,
    val link: String,
    val public: Boolean
)
