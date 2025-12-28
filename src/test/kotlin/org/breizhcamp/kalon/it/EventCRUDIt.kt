package org.breizhcamp.kalon.it

import org.breizhcamp.kalon.config.multitenant.TenantName
import org.breizhcamp.kalon.helpers.EventHelper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient

class EventCRUDIt: AbstractItTest() {

    @Test
    fun `should create, read, update and delete an event`(@Autowired webClient: RestTestClient) {
        val tenantName = TenantName("breizhcamp")
        val client = createClient(webClient, tenantName, getAdminToken(tenantName))

        val event = EventHelper.get()
        val eventId = event.id.value

        // Check id is available
        client.head()
            .uri("/events/{id}", eventId)
            .exchange()
            .expectStatus().isNotFound

        // Create Event
        client.post()
            .uri("/events")
            .body(event)
            .exchange()
            .expectStatus().isCreated()

        // List Events
        client.listEvents().jsonPath("$[?(@.id == '$eventId')]").exists()

        // Check id is not available anymore
        client.head()
            .uri("/events/{id}", eventId)
            .exchange()
            .expectStatus().isOk

        // Update Event
        client.put()
            .uri("/events/{id}", eventId)
            .body(event.copy(name = "BreizhCamp 2025 - Updated"))
            .exchange()
            .expectStatus().isNoContent()

        // Check update
        client.listEvents()
            .jsonPath("$[?(@.id == '$eventId' && @.name == 'BreizhCamp 2025 - Updated')]").exists()

        // Check other tenant that the event does not exist
        val jscTenant = TenantName("jsc")
        val jscClient = createClient(webClient, jscTenant, getAdminToken(jscTenant))
        jscClient.listEvents().json("[]")

        // Delete Event
        client.delete()
            .uri("/events/{id}", eventId)
            .exchange()
            .expectStatus().isNoContent()

        // Check deletion
        client.listEvents().jsonPath("$[?(@.id == '$eventId')]").doesNotExist()
    }

    @Test
    fun `test user permissions`(@Autowired webClient: RestTestClient) {
        val tenantName = TenantName("breizhcamp")
        val client = createClient(webClient, tenantName, getUserToken(tenantName))
        val event = EventHelper.get()

        // Try to create Event (should fail)
        client.post()
            .uri("/events")
            .body(event)
            .exchange()
            .expectStatus().isForbidden

        // List Events (should succeed)
        client.listEvents()

        // Try to update Event (should fail)
        client.put()
            .uri("/events/{id}", event.id.value)
            .body(event)
            .exchange()
            .expectStatus().isForbidden

        // Try to delete Event (should fail)
        client.delete()
            .uri("/events/{id}", event.id.value)
            .exchange()
            .expectStatus().isForbidden
    }

    private fun RestTestClient.listEvents(): RestTestClient.BodyContentSpec = this.get()
        .uri("/events")
        .exchange()
        .expectStatus().isOk
        .expectBody()

}
