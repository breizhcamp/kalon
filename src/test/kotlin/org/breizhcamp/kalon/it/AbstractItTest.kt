package org.breizhcamp.kalon.it

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Testcontainers
@AutoConfigureRestTestClient
abstract class AbstractItTest {

    fun getAdminToken(): String = getToken("kalon-admin")
    fun getUserToken(): String = getToken("kalon-user")

    fun getToken(clientId: String): String {
        val client = RestClient.create(getIssuerUri())
        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "password")
        params.add("username", "user")
        params.add("password", "password")
        params.add("client_id", clientId)

        return client.post()
            .uri("/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body<TokenResponse>()?.accessToken
            ?: error("No token returned")
    }

    companion object {

        @Container
        @ServiceConnection
        @JvmStatic
        val postgresql = PostgreSQLContainer("postgres:15.2")

        @Container
        @JvmStatic
        val oAuth2Server = GenericContainer(DockerImageName.parse("ghcr.io/navikt/mock-oauth2-server:3.0.1"))
            .withExposedPorts(8080)
            .withEnv("JSON_CONFIG_PATH", "/config/mock-oauth2-server.json")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("oauth2-mock-server-config.json"),
                "/config/mock-oauth2-server.json"
            )

        @JvmStatic
        fun getIssuerUri(): String {
            return "http://${oAuth2Server.host}:${oAuth2Server.getMappedPort(8080)}/kalon"
        }

        @DynamicPropertySource
        @JvmStatic
        fun oAuth2Properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { getIssuerUri() }
        }
    }

    data class TokenResponse(
        @JsonProperty("access_token")
        val accessToken: String,
    )
}
