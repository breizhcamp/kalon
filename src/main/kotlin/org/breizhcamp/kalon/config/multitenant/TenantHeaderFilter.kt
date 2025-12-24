package org.breizhcamp.kalon.config.multitenant

import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(-200) // Before Spring Security Filter Chain
class TenantHeaderFilter(
    tenantIdResolver: TenantIdResolver,
    private val tenantRepo: TenantRepo,
): TenantFilter(tenantIdResolver) {

    override fun getTenantId(request: ServletRequest): Tenant? {
        val req = request as? HttpServletRequest ?: return null

        val tenantHost = req.getHeader("X-Tenant-Host")?.let { tenantRepo.fromHost(it) }
        val tenantName = req.getHeader("X-Tenant")?.let { tenantRepo.fromName(TenantName(it)) }

        return tenantHost ?: tenantName
    }

}
