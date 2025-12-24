package org.breizhcamp.kalon.domain.entities

sealed class ModuleConfig {}

data class OrgaModuleConfig(
    val auth: ModuleAuthConfig,
): ModuleConfig()

data class ModuleAuthConfig (
    val url: String,
    val realm: String,
    val clientId: String,
)
