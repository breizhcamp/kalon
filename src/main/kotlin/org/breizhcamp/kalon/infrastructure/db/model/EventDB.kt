package org.breizhcamp.kalon.infrastructure.db.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "event")
data class EventDB(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val year: Int,
    val name: String?,
    @Temporal(TemporalType.DATE)
    val debutEvent: LocalDate?,
    @Temporal(TemporalType.DATE)
    val finEvent: LocalDate?,
    @Column(name = "debut_cfp")
    @Temporal(TemporalType.DATE)
    val debutCFP: LocalDate?,
    @Column(name = "fin_cfp")
    @Temporal(TemporalType.DATE)
    val finCFP: LocalDate?,
    @Temporal(TemporalType.DATE)
    val debutInscription: LocalDate?,
    @Temporal(TemporalType.DATE)
    val finInscription: LocalDate?,
    val website: String?,
)
