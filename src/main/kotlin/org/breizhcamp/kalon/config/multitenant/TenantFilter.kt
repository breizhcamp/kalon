package org.breizhcamp.kalon.config.multitenant

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.breizhcamp.kalon.config.log.KalonMDC
import org.slf4j.MDC

abstract class TenantFilter(
    private val tenantIdResolver: TenantIdResolver,
): Filter {

    abstract fun getTenantId(request: ServletRequest): Tenant?

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        getTenantId(request)?.let {
            tenantIdResolver.current = it
            MDC.put(KalonMDC.TENANT, it.name.value)
        }

        try {
            chain.doFilter(request, response)
        } finally {
            tenantIdResolver.clear()
            MDC.remove(KalonMDC.TENANT)
        }
    }

}