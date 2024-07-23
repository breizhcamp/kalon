package org.breizhcamp.kalon.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.session.HttpSessionEventPublisher

@Configuration
@Profile(value = ["default", "dev"])
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun sessionRegistry(): SessionRegistry = SessionRegistryImpl()

    @Bean
    fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
        RegisterSessionAuthenticationStrategy(sessionRegistry())

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher =
        HttpSessionEventPublisher()

    @Bean
    fun resourceServerFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .oauth2ResourceServer {
                it.jwt { config ->
                    config.jwtAuthenticationConverter { jwt ->
                        JwtAuthenticationToken(jwt, getRolesFromJwt(jwt))
                    }
                }
            }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.GET, "/api/**").hasRole("KALON") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN") }
            // Filter methods are called with a POST http verb, but should be able to be called by everyone who has access to the app
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.POST, "/api/**/filter/**").hasRole("KALON") }
            // These requests can be done by both admins and the member which owns the contact methods, so the authorization is handled in the controller layer
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.POST, "/api/**/contact/**").hasRole("KALON") }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.DELETE, "/api/**/contact/**").hasRole("KALON") }
            .oauth2Client(Customizer.withDefaults())
            .build()
    }

    private fun getRolesFromJwt(jwt: Jwt): Collection<GrantedAuthority> =
        generateAuthoritiesForClaim(getRolesFromClaims(jwt.claims))

    private fun getRolesFromRealmAccess(realmAccess: Map<String, Any>): Collection<String> {
        val roles = realmAccess["roles"]

        if (roles is Collection<*>) {
            return roles.map {
                if (it is String) {
                    it
                } else throw IllegalArgumentException("Role was not of type String")
            }
        }

        throw IllegalArgumentException("Roles were not a Collection of String")
    }

    private fun getRolesFromClaims(attributes: MutableMap<String, Any>): Collection<String> {
        val realmAccess = attributes["realm_access"]

        if (realmAccess is MutableMap<*, *>) {
            if (realmAccess.all { (k, _) -> (k is String) }) {
                return getRolesFromRealmAccess(realmAccess
                    .mapKeys { (k, _) -> k as String }
                    .mapValues { (_, v) -> v as Any }
                )
            }
        }

        throw IllegalArgumentException("Realm access was not a Map of String to Collection of String")
    }

    private fun generateAuthoritiesForClaim(roles: Collection<String>): Collection<GrantedAuthority> =
        roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
}

@Configuration
@Profile("test")
@EnableWebSecurity
class NoAuthSecurityConfig {
    @Bean
    fun resourceServerFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf { it.disable() }
            .authorizeHttpRequests{ it.anyRequest().permitAll() }
            .build()
    }
}