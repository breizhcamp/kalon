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
    val issuerUri: String,
    val jwksUri: String? = null,
)
