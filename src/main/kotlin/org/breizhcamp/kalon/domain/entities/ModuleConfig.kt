package org.breizhcamp.kalon.domain.entities

sealed class ModuleConfig

data class OrgaModuleConfig(
    val auth: ModuleAuthConfig,
    val backends: List<ModuleBackendConfig>,
): ModuleConfig()

data class SponsorModuleConfig(
    val auth: ModuleAuthConfig,
    val backends: List<ModuleBackendConfig>,
    val defaultEventId: EventId,
): ModuleConfig()

data class ModuleAuthConfig (
    val url: String,
    val realm: String,
    val clientId: String,
)

data class ModuleBackendConfig(
    val name: String,
    val url: String,
)
