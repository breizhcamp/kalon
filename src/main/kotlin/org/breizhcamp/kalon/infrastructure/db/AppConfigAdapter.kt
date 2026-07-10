package org.breizhcamp.kalon.infrastructure.db

import org.breizhcamp.kalon.config.annotations.Adapter
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.ports.AppConfigPort
import org.breizhcamp.kalon.infrastructure.db.repos.AppConfigRepo

@Adapter
class AppConfigAdapter(
    private val appConfigRepo: AppConfigRepo,
): AppConfigPort {
    override fun setDefaultEvent(eventId: EventId) {
        val appConfig = appConfigRepo.get()
        appConfig.defaultEventId = eventId.value
        appConfigRepo.save(appConfig)
    }
}
