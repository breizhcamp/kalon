package org.breizhcamp.kalon.config.multitenant

import jakarta.servlet.ServletRequest
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(-200) // Before Spring Security Filter Chain
class TenantHostFilter(
    tenantIdResolver: TenantIdResolver,
    private val tenantRepo: TenantRepo,
): TenantFilter(tenantIdResolver) {

    override fun getTenantId(request: ServletRequest): Tenant? {
        var host = request.serverName
        if (request.serverPort != 443) host += ":${request.serverPort}"

        return tenantRepo.fromHost(host)
    }
}
