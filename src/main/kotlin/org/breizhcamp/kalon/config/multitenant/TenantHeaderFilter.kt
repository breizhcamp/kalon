package org.breizhcamp.kalon.config.multitenant

import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(-200) // Before Spring Security Filter Chain
@Profile("it")
class TenantHeaderFilter(
    tenantIdResolver: TenantIdResolver,
    private val tenantRepo: TenantRepo,
): TenantFilter(tenantIdResolver) {

    override fun getTenantId(request: ServletRequest): Tenant? =
        (request as? HttpServletRequest)
            ?.getHeader("X-Tenant")
            ?.let { tenantRepo.fromName(TenantName(it)) }

}
