package org.breizhcamp.kalon.config.multitenant

import org.breizhcamp.kalon.config.TenantConfig

@JvmInline
value class TenantName(val value: String)

data class Tenant(
    val name: TenantName,
    val domain: String,
    val schema: String,
    val issuerUri: String,
    /** URL that contains the public key for verifying the JWT Signature */
    val jwksUri: String?,
)

fun TenantConfig.toTenant() = Tenant(
    name = TenantName(name),
    domain = domain,
    schema = schema,
    issuerUri = auth.issuerUri,
    jwksUri = auth.jwksUri,
)
