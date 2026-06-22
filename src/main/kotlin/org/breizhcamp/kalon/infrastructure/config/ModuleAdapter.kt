package org.breizhcamp.kalon.infrastructure.config

import org.breizhcamp.kalon.config.KalonConfig
import org.breizhcamp.kalon.config.TenantBackend
import org.breizhcamp.kalon.config.TenantConfig
import org.breizhcamp.kalon.config.TenantModule
import org.breizhcamp.kalon.config.annotations.Adapter
import org.breizhcamp.kalon.domain.entities.ModuleAuthConfig
import org.breizhcamp.kalon.domain.entities.ModuleBackendConfig
import org.breizhcamp.kalon.domain.entities.ModuleConfig
import org.breizhcamp.kalon.domain.entities.OrgaModuleConfig
import org.breizhcamp.kalon.domain.entities.SponsorModuleConfig
import org.breizhcamp.kalon.domain.ports.ModulePort

@Adapter
class ModuleAdapter(
    private val config: KalonConfig,
): ModulePort {

    override fun getFromHost(host: String): ModuleConfig? {
        val (tenant, module) = getFromConfig(host) ?: return null

        return when (module.name) {
            "orga" -> OrgaModuleConfig(
                auth = module.toAuthDomain(tenant),
                backends = tenant.backends.map { it.toDomain() }
            )
            "sponsor" -> SponsorModuleConfig(
                backends = tenant.backends.map { it.toDomain() },
            )
            else -> null
        }
    }

    private fun getFromConfig(host: String): Pair<TenantConfig, TenantModule>? {
        for (tenant in config.tenants.config) {
            for (module in tenant.modules) {
                if (module.domain == host) {
                    return tenant to module
                }
            }
        }
        return null
    }

    private fun TenantModule.toAuthDomain(tenant: TenantConfig) = ModuleAuthConfig(
        url = tenant.auth.url,
        realm = tenant.auth.realm,
        clientId = authClientId,
    )

    private fun TenantBackend.toDomain() = ModuleBackendConfig(
        name = name,
        url = url,
    )
}
