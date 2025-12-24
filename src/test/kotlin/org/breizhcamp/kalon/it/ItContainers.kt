package org.breizhcamp.kalon.it

import org.breizhcamp.kalon.it.AbstractItTest.Companion.oAuth2Server
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

@TestConfiguration(proxyBeanMethods = false)
class ItContainers {

    @Bean
    @ServiceConnection
    fun postgresqlContainer(): PostgreSQLContainer =
        PostgreSQLContainer("postgres:15.2")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init-pg-schema.sql"),
                "/docker-entrypoint-initdb.d/init.sql"
            )

    @Bean
    fun oAuth2ServerContainer(): GenericContainer<*> = oAuth2Server

}