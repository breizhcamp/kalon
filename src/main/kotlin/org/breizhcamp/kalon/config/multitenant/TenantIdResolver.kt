package org.breizhcamp.kalon.config.multitenant

import org.breizhcamp.kalon.config.KalonConfig
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component

@Component
class TenantIdResolver(
    private val config: KalonConfig
): CurrentTenantIdentifierResolver<Tenant>, HibernatePropertiesCustomizer {

    private val defaultTenant = config.tenants.config.find { it.name == config.tenants.default }?.toTenant()
        ?: throw IllegalStateException("Default tenant '${config.tenants.default}' not found in configuration")

    private val localCurrentTenant = ThreadLocal<Tenant>()

    var current: Tenant?
        get() = localCurrentTenant.get()
        set(tenant) = localCurrentTenant.set(tenant)

    val tenant: String
        get() = requireNotNull(current?.name) { "No current tenant" }

    fun clear() {
        localCurrentTenant.remove()
    }

    override fun resolveCurrentTenantIdentifier(): Tenant {
        return current ?: defaultTenant
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return true
    }

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
    }
}
