package org.breizhcamp.kalon.config.multitenant

import io.github.oshai.kotlinlogging.KotlinLogging
import liquibase.integration.spring.SpringLiquibase
import org.breizhcamp.kalon.config.KalonConfig
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

@Component
class LiquibaseConfig: ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val tenants = event.applicationContext.getBean<KalonConfig>().tenants.config
        val dataSource = event.applicationContext.getBean<DataSource>()
        tenants.forEach {
            logger.info { "Running liquibase for tenant [${it.name}]" }
            createLiquibase(it.name, dataSource).afterPropertiesSet()
        }
    }

    private fun createLiquibase(tenant: String, ds: DataSource): SpringLiquibase {
        return SpringLiquibase().apply {
            changeLog = "classpath:db/changelog/db.changelog-master.xml"
            dataSource = ds
            defaultSchema = tenant
        }
    }

}
