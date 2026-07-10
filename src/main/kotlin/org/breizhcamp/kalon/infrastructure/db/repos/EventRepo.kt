package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.infrastructure.db.model.EventDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventRepo: JpaRepository<EventDB, String> {
    fun findAllByOrderByStartDateDesc(): List<EventDB>

    @Query(
        "SELECT event " +
        "FROM EventDB event " +
        "INNER JOIN AppConfigDB app_config " +
        "ON app_config.defaultEventId = event.id",
    )
    fun getDefaultEvent(): EventDB?
}
