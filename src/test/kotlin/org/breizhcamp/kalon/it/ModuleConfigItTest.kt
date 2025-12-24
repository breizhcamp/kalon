package org.breizhcamp.kalon.it

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient
import kotlin.test.Test

class ModuleConfigItTest: AbstractItTest() {

    @Test
    fun `should retrieve orga module configuration from breizhcamp`(@Autowired webClient: RestTestClient) {
        webClient.get()
            .uri("/modules/config")
            .header("X-Tenant-Host", "orga.breizhcamp.org")
            .exchange()
            .expectStatus().isOk
            .expectBody().json("""[
                {"key":"KEYCLOAK_URL", "value":"${getOauthUri()}/breizhcamp"},
                {"key":"KEYCLOAK_REALM", "value":"breizhcamp"},
                {"key":"KEYCLOAK_CLIENT_ID", "value":"orga-front"},
                {"key":"KALON_URL", "value":"https://kalon.breizhcamp.org"}
            ]""".trimIndent())
    }
}