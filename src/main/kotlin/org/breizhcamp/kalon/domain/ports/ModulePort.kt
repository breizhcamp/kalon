package org.breizhcamp.kalon.domain.ports

import org.breizhcamp.kalon.domain.entities.ModuleConfig

interface ModulePort {

    fun getFromHost(host: String): ModuleConfig?

}