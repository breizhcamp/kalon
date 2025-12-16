package org.breizhcamp.kalon.config.multitenant

import org.breizhcamp.kalon.config.KalonConfig
import org.springframework.stereotype.Component

/** Read tenants from configuration and expose them */
@Component
class TenantRepo(
    private val config: KalonConfig,
) {

    private val tenants = config.tenants.config.associate { it.domain to it.toTenant() }
    val default = tenants.values.find { it.name == config.tenants.default }
        ?: throw IllegalStateException("Default tenant '${config.tenants.default}' not found in configuration")

    fun getTenant(host: String): Tenant? {
        return tenants[host]
    }

    fun getFromIssuerUri(iss: String): Tenant? {
        return tenants.values.find { it.issuerUri == iss }
    }
}
