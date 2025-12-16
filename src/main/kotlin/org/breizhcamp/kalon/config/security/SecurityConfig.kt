package org.breizhcamp.kalon.config.security

import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.jwt.proc.JWTProcessor
import org.breizhcamp.kalon.config.multitenant.TenantJWSKeySelector
import org.breizhcamp.kalon.config.multitenant.TenantJwtIssuerValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
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
    fun jwtProcessor(keySelector: TenantJWSKeySelector): JWTProcessor<SecurityContext> {
        val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
        jwtProcessor.jwtClaimsSetAwareJWSKeySelector = keySelector
        return jwtProcessor
    }

    @Bean
    fun jwtDecoder(jwtProcessor: JWTProcessor<SecurityContext>?, jwtValidator: TenantJwtIssuerValidator): JwtDecoder {
        val decoder = NimbusJwtDecoder(jwtProcessor)
        val validator = DelegatingOAuth2TokenValidator(JwtValidators.createDefault(), jwtValidator)
        decoder.setJwtValidator(validator)
        return decoder
    }

    @Bean
    fun web(http: HttpSecurity, jwtTenantDecoder: JwtDecoder): SecurityFilterChain {

        http {
            addFilterAfter<SwitchUserFilter>(UserMDCFilter())

            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = jwtAuthenticationConverter()
                    jwtDecoder = jwtTenantDecoder
                }
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
