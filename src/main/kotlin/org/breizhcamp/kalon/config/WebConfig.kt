package org.breizhcamp.kalon.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig(
    private val config: KalonConfig,
): WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        val domains = config.tenants.config.flatMap { it.modules }.map { it.domain }
            .map { if (it.contains(":")) "http://$it" else "https://$it" }
            .toTypedArray()

        registry.addMapping("/**")
            .allowedOrigins(*domains)
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
    }

}