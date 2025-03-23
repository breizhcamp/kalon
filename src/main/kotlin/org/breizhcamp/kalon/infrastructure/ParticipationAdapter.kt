package org.breizhcamp.kalon.infrastructure

import org.breizhcamp.kalon.domain.use_cases.ports.ParticipationPort
import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDB
import org.breizhcamp.kalon.infrastructure.db.model.ParticipationDBId
import org.breizhcamp.kalon.infrastructure.db.repos.ParticipationRepo
import org.springframework.stereotype.Component
import java.util.*

@Component
class ParticipationAdapter (
    private val participationRepo: ParticipationRepo
): ParticipationPort {

    override fun existsByIds(teamId: UUID, memberId: UUID, eventId: Int): Boolean =
        participationRepo.existsById(ParticipationDBId(
            memberId,
            teamId,
            eventId
        ))

    override fun createByIds(teamId: UUID, memberId: UUID, eventId: Int) {
        participationRepo.save(
            ParticipationDB(
                ParticipationDBId(
                    memberId,
                    teamId,
                    eventId
                )
            )
        )
    }


    override fun removeByIds(teamId: UUID, memberId: UUID, eventId: Int) =
        participationRepo.deleteById(ParticipationDBId(
            memberId,
            teamId,
            eventId
        ))

}