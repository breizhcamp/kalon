package org.breizhcamp.kalon.config.multitenant

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector
import org.springframework.stereotype.Component
import java.net.URI
import java.security.Key
import java.util.concurrent.ConcurrentHashMap

@JvmInline
value class IssuerUri(val uri: String)

@Component
class TenantJWSKeySelector(
    private val tenantRepo: TenantRepo,
): JWTClaimsSetAwareJWSKeySelector<SecurityContext> {
    //TODO use a real cache with expiration
    private val selectors: MutableMap<IssuerUri, JWSKeySelector<SecurityContext?>> = ConcurrentHashMap()


    override fun selectKeys(jwsHeader: JWSHeader?, jwtClaimsSet: JWTClaimsSet, securityContext: SecurityContext?): List<Key?> {
        val issuerUri = getIssuerUri(jwtClaimsSet)

        return selectors
            .computeIfAbsent(issuerUri) { fromIssuer(it) }
            .selectJWSKeys(jwsHeader, securityContext)
    }

    private fun getIssuerUri(claimSet: JWTClaimsSet): IssuerUri {
        return IssuerUri(requireNotNull(claimSet.getClaim("iss") as? String) { "No 'iss' claim in JWT Token" })
    }

    private fun fromIssuer(issuerUri: IssuerUri): JWSKeySelector<SecurityContext?> {
        val tenant = tenantRepo.getFromIssuerUri(issuerUri.uri)
            ?: throw IllegalArgumentException("No tenant found for issuer '$issuerUri'")

        //TODO retrieve jwksUri from well-known endpoint if not defined
        val jwksUri = requireNotNull(tenant.jwksUri) { "No jwksUri in tenant '${tenant.name}'" }
        return fromUri(jwksUri)
    }

    private fun fromUri(uri: String): JWSKeySelector<SecurityContext?> {
        return try {
            // call the Auth Server to retrieve the JWK Set
            JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(URI.create(uri).toURL())
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex)
        }
    }
}
