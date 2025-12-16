package org.breizhcamp.kalon

import org.breizhcamp.kalon.config.KalonConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [LiquibaseAutoConfiguration::class])
@EnableConfigurationProperties(KalonConfig::class)
class KalonApplication

fun main(args: Array<String>) {
	runApplication<KalonApplication>(*args)
}
