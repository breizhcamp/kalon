package org.breizhcamp.kalon.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.breizhcamp.kalon.domain.entities.ModuleAuthConfig
import org.breizhcamp.kalon.domain.entities.ModuleBackendConfig
import org.breizhcamp.kalon.domain.entities.ModuleConfig
import org.breizhcamp.kalon.domain.entities.OrgaModuleConfig

data class ModuleConfigAPI(
    @field:Schema(description = "Configuration key")
    val key: String,
    @field:Schema(description = "Configuration value")
    val value: String,
)

fun List<Pair<String, String>>.toModuleConfigAPI(): List<ModuleConfigAPI> =
    this.map { (key, value) -> ModuleConfigAPI(key, value) }

fun ModuleConfig.toApi(): List<ModuleConfigAPI> = when (this) {
    is OrgaModuleConfig -> this.auth.toApi() + this.backends.flatMap { it.toApi() }
}

fun ModuleAuthConfig.toApi(): List<ModuleConfigAPI> = listOf(
    "KEYCLOAK_URL" to url,
    "KEYCLOAK_REALM" to realm,
    "KEYCLOAK_CLIENT_ID" to clientId,
).toModuleConfigAPI()

fun ModuleBackendConfig.toApi(): List<ModuleConfigAPI> = listOf(
    "${name.uppercase()}_URL" to url
).toModuleConfigAPI()