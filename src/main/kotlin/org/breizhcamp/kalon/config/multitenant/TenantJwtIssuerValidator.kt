package org.breizhcamp.kalon.config.multitenant

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class TenantJwtIssuerValidator(
    private val tenantIdResolver: TenantIdResolver,
): OAuth2TokenValidator<Jwt> {
    private val error = OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "The iss claim is not valid", "https://tools.ietf.org/html/rfc6750#section-3.1")

    override fun validate(token: Jwt): OAuth2TokenValidatorResult {
        val tenant = requireNotNull(tenantIdResolver.current) { "No current tenant when validating JWT iss" }
        return if (token.issuer.toExternalForm() == tenant.issuerUri)
            OAuth2TokenValidatorResult.success()
        else
            OAuth2TokenValidatorResult.failure(error)
    }
}
