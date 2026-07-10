package org.breizhcamp.kalon.infrastructure.db.repos

import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.infrastructure.db.model.AppConfigDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AppConfigRepo: JpaRepository<AppConfigDB, Int> {
    @Query(
        "SELECT app_config " +
        "FROM AppConfigDB app_config " +
        "WHERE app_config.id = 1",
    )
    fun get(): AppConfigDB

    @Query(
        "SELECT app_config.defaultEventId " +
        "FROM AppConfigDB app_config " +
        "WHERE app_config.id = 1",
    )
    fun getDefaultEventId(): EventId?
}
