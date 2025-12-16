package org.breizhcamp.kalon.config.multitenant

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.breizhcamp.kalon.config.log.KalonMDC
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(-200) // Before Spring Security Filter Chain
class TenantFilter(
    private val tenantIdResolver: TenantIdResolver,
    private val tenantRepo: TenantRepo,
): Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val host = request.serverName
        tenantRepo.getTenant(host)?.let {
            tenantIdResolver.current = it
            MDC.put(KalonMDC.TENANT, it.name)
        }

        try {
            chain.doFilter(request, response)
        } finally {
            tenantIdResolver.clear()
            MDC.remove(KalonMDC.TENANT)
        }
    }
}
