package org.breizhcamp.kalon.config.multitenant

import org.breizhcamp.kalon.config.KalonConfig
import org.springframework.stereotype.Component

/** Read tenants from configuration and expose them */
@Component
class TenantRepo(
    private val config: KalonConfig,
) {

    private val tenants = initTenants()
    val default = tenants.values.find { it.name.value == config.tenants.default }
        ?: throw IllegalStateException("Default tenant '${config.tenants.default}' not found in configuration")

    fun initTenants(): Map<String, Tenant> {
        return config.tenants.config.associate { it.domain to it.toTenant() } +
                config.tenants.config
                    .flatMap { t -> t.modules.map { it.domain to t.toTenant() } }
    }

    fun fromHost(host: String): Tenant? = tenants[host]

    fun fromName(name: TenantName): Tenant? =
        tenants.values.find { it.name.value == name.value }

    fun getFromIssuerUri(iss: String): Tenant? =
        tenants.values.find { it.issuerUri == iss }
}
