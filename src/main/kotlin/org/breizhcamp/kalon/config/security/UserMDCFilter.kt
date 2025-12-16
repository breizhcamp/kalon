package org.breizhcamp.kalon.config.security

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder

class UserMDCFilter: Filter {
    companion object {
        const val LOGIN = "user.login"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        getLogin()?.let { MDC.put(LOGIN, it) }

        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove(LOGIN)
        }
    }

    private fun getLogin() = SecurityContextHolder.getContext().authentication?.name
}
