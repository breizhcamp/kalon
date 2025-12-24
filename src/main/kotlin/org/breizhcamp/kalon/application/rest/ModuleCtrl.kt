package org.breizhcamp.kalon.application.rest

import io.swagger.v3.oas.annotations.tags.Tag
import org.breizhcamp.kalon.application.dto.ModuleConfigAPI
import org.breizhcamp.kalon.application.dto.toApi
import org.breizhcamp.kalon.domain.use_cases.module.RetrieveModuleConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/modules", produces = ["application/json"])
@Tag(name = "modules", description = "Module related operations")
class ModuleCtrl(
    private val retrieveModuleConfig: RetrieveModuleConfig,
) {

    @GetMapping("/config")
    fun getConfig(@RequestHeader("X-Tenant-Host") tenantHost: String): List<ModuleConfigAPI> {
        return retrieveModuleConfig.retrieveFromHost(tenantHost).toApi()
    }

}