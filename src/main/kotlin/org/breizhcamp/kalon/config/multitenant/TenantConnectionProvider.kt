package org.breizhcamp.kalon.config.multitenant

import org.hibernate.cfg.AvailableSettings
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class TenantConnectionProvider(
    private val dataSource: DataSource,
    private val tenantRepo: TenantRepo,
): MultiTenantConnectionProvider<Tenant>, HibernatePropertiesCustomizer {

    override fun getAnyConnection(): Connection {
        return dataSource.connection
    }

    override fun releaseAnyConnection(connection: Connection?) {
        connection?.close()
    }

    override fun getConnection(tenantIdentifier: Tenant?): Connection {
        return dataSource.connection.apply {
            schema = tenantIdentifier?.schema ?: tenantRepo.default.schema
        }
    }

    override fun releaseConnection(tenantIdentifier: Tenant?, connection: Connection?) {
        connection?.apply {
            schema = tenantRepo.default.schema
            close()
        }
    }

    override fun supportsAggressiveRelease(): Boolean {
        return false
    }

    override fun isUnwrappableAs(unwrapType: Class<*>): Boolean {
        return ConnectionProvider::class.java.isAssignableFrom(unwrapType) ||
                MultiTenantConnectionProvider::class.java.isAssignableFrom(unwrapType)
    }

    override fun <T> unwrap(unwrapType: Class<T>): T {
        return dataSource.unwrap(unwrapType)
    }

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER] = this
    }

}
