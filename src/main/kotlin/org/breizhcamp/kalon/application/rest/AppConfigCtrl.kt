package org.breizhcamp.kalon.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.breizhcamp.kalon.application.dto.EventFullAPI
import org.breizhcamp.kalon.application.dto.SetDefaultEventAPI
import org.breizhcamp.kalon.application.dto.toFullApi
import org.breizhcamp.kalon.config.security.IsAdmin
import org.breizhcamp.kalon.config.security.IsUser
import org.breizhcamp.kalon.domain.use_cases.AppConfigCRUD
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config", produces = ["application/json"])
@Tag(name = "config", description = "Config related operations")
class AppConfigCtrl(
    private val appConfigCRUD: AppConfigCRUD,
) {
    @Operation(summary = "Get the default event")
    @GetMapping("/default-event")
    @IsUser
    fun getDefaultEvent(): EventFullAPI =
        appConfigCRUD.getDefaultEvent().toFullApi()

    @Operation(summary = "Set the default event")
    @PutMapping("/default-event")
    @IsAdmin
    fun setDefaultEvent(
        @Valid
        @RequestBody setDefaultEventAPI: SetDefaultEventAPI,
    ) {
        appConfigCRUD.setDefaultEvent(setDefaultEventAPI.defaultEventId)
    }
}
