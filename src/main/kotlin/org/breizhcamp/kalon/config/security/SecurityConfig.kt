package org.breizhcamp.kalon.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private fun jwtAuthenticationConverter() = JwtAuthenticationConverter().apply {
        setPrincipalClaimName("preferred_username")
        setJwtGrantedAuthoritiesConverter(JwtAuthoritiesConverter())
    }

    @Bean
    fun web(http: HttpSecurity): SecurityFilterChain {
        http {
            addFilterAfter<SwitchUserFilter>(UserMDCFilter())

            oauth2ResourceServer {
                jwt { jwtAuthenticationConverter = jwtAuthenticationConverter() }
            }

            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

            authorizeHttpRequests {
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize(anyRequest, authenticated)
            }

            csrf { disable() }
        }

        return http.build()
    }

}
