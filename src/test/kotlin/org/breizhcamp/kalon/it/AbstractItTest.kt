package org.breizhcamp.kalon.it

import com.fasterxml.jackson.annotation.JsonProperty
import org.breizhcamp.kalon.config.TenantAuth
import org.breizhcamp.kalon.config.TenantConfig
import org.breizhcamp.kalon.config.TenantModule
import org.breizhcamp.kalon.config.multitenant.TenantName
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

@Import(ItContainers::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Testcontainers
@AutoConfigureRestTestClient
abstract class AbstractItTest {

    fun getAdminToken(tenant: TenantName): String = getToken(tenant, "kalon-admin")
    fun getUserToken(tenant: TenantName): String = getToken(tenant, "kalon-user")

    fun getToken(tenant: TenantName, clientId: String): String {
        val client = RestClient.create(getOauthUri())
        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "password")
        params.add("username", "user")
        params.add("password", "password")
        params.add("client_id", clientId)

        return client.post()
            .uri("{tenant}/token", tenant.value)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body<TokenResponse>()?.accessToken
            ?: error("No token returned")
    }

    fun createClient(webClient: RestTestClient, tenant: TenantName, authToken: String): RestTestClient =
        webClient.mutate()
            .defaultHeader("Authorization", "Bearer $authToken")
            .defaultHeader("X-Tenant", tenant.value)
            .build()

    companion object {

        @JvmStatic
        val oAuth2Server: GenericContainer<*> = GenericContainer(DockerImageName.parse("ghcr.io/navikt/mock-oauth2-server:3.0.1"))
            .withExposedPorts(8080)
            .withEnv("JSON_CONFIG_PATH", "/config/mock-oauth2-server.json")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("oauth2-mock-server-config.json"),
                "/config/mock-oauth2-server.json"
            )
            .apply { start() }

        @JvmStatic
        fun getOauthUri(): String {
            return "http://${oAuth2Server.host}:${oAuth2Server.getMappedPort(8080)}"
        }

        @DynamicPropertySource
        @JvmStatic
        fun oAuth2TenantProperties(registry: DynamicPropertyRegistry) {
            registry.add("kalon.tenants.default") { "breizhcamp" }

            val breizhcamp = TenantConfig(
                name = "breizhcamp",
                domain = "breizhcamp.org", //not used in tests, get the Tenant from the header
                schema = "breizhcamp",
                auth = TenantAuth(
                    issuerUri = getOauthUri() + "/breizhcamp",
                    jwksUri = getOauthUri() + "/breizhcamp/jwks",
                    realm = "breizhcamp",
                ),
                modules = listOf(
                    TenantModule("orga", "orga.breizhcamp.org", "orga-front")
                )
            )

            val jsc = TenantConfig(
                name = "jsc",
                domain = "jsc.org",  //not used in tests, get the Tenant from the header
                schema = "jsc",
                auth = TenantAuth(
                    issuerUri = getOauthUri() + "/jsc",
                    jwksUri = getOauthUri() + "/jsc/jwks",
                    realm = "jsc"
                ),
                modules = listOf(
                    TenantModule("orga", "orga.jsc.org", "orga-front")
                )
            )

            registry.add("kalon.tenants.config") { listOf(breizhcamp, jsc) }
        }
    }

    data class TokenResponse(
        @JsonProperty("access_token")
        val accessToken: String,
    )
}
