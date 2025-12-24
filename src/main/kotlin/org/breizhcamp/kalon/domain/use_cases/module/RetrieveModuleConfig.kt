package org.breizhcamp.kalon.domain.use_cases.module

import org.breizhcamp.kalon.config.annotations.UseCase
import org.breizhcamp.kalon.domain.entities.ModuleConfig
import org.breizhcamp.kalon.domain.exceptions.NotFoundException
import org.breizhcamp.kalon.domain.ports.ModulePort

@UseCase
class RetrieveModuleConfig(
    private val modulePort: ModulePort,
) {

    fun retrieveFromHost(host: String): ModuleConfig =
        modulePort.getFromHost(host) ?: throw NotFoundException<ModuleConfig>(host)

}