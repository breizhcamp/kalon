package org.breizhcamp.kalon.config.multitenant

import org.breizhcamp.kalon.config.TenantConfig

data class Tenant(
    val name: String,
    val domain: String,
    val schema: String,
    val issuerUri: String,
    /** URL that contains the public key for verifying the JWT Signature */
    val jwksUri: String?,
)

fun TenantConfig.toTenant() = Tenant(
    name = this.name,
    domain = this.domain,
    schema = this.schema,
    issuerUri = this.issuerUri,
    jwksUri = jwksUri,
)
