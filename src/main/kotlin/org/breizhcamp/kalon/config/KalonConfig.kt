package org.breizhcamp.kalon.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kalon")
data class KalonConfig(
    val tenants: TenantsConfig,
)

data class TenantsConfig(
    val default: String,
    val config: List<TenantConfig>,
)

data class TenantConfig(
    val name: String,
    val domain: String,
    val schema: String,
    val auth: TenantAuth,
    val modules: List<TenantModule> = emptyList(),
    val backends: List<TenantBackend> = emptyList(),
)

data class TenantAuth(
    val url: String,
    val issuerUri: String,
    val jwksUri: String? = null,
    val realm: String,
)

data class TenantModule(
    val name: String,
    val domain: String,
    val authClientId: String,
)

data class TenantBackend(
    val name: String,
    val url: String,
)