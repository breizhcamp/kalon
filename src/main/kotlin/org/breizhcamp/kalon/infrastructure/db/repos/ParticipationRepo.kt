package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDB
import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDBId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipationRepo: JpaRepository<ParticipationDB, ParticipationDBId>