package org.breizhcamp.kalon.config.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JwtAuthoritiesConverter: Converter<Jwt, Collection<GrantedAuthority>> {
    companion object {
        const val JWT_ROLE_PREFIX = "kalon_"
    }

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val access = jwt.claims["realm_access"] as? Map<*, *>
        val roles = access?.get("roles") as? List<*> ?: return emptyList()

        return roles
            .filterIsInstance<String>()
            .filter { role -> role.startsWith(JWT_ROLE_PREFIX) }
            .map { it.substring(JWT_ROLE_PREFIX.length) }
            .map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
    }
}
