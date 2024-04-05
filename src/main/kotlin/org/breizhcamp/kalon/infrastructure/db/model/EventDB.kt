package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event")
data class EventDB(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val year: Int,
    val name: String?,
    val debutEvent: LocalDateTime?,
    val finEvent: LocalDateTime?,
    @Column(name = "debut_cfp")
    val debutCFP: LocalDateTime?,
    @Column(name = "fin_cfp")
    val finCFP: LocalDateTime?,
    val debutInscription: LocalDateTime?,
    val finInscription: LocalDateTime?,
    val website: String?,
)
