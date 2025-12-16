package org.breizhcamp.kalon.it

import org.breizhcamp.kalon.helpers.EventHelper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient

class EventCRUDIt: AbstractItTest() {

    @Test
    fun `should create, read, update and delete an event`(@Autowired webClient: RestTestClient) {
        val token = getAdminToken()
        val client = webClient.mutate().defaultHeader("Authorization", "Bearer $token").build()

        val event = EventHelper.get()
        val eventId = event.id.value

        // Create Event
        client.post()
            .uri("/events")
            .body(event)
            .exchange()
            .expectStatus().isCreated()

        // List Events
        client.listEvents().jsonPath("$[?(@.id == '$eventId')]").exists()

        // Update Event
        client.put()
            .uri("/events")
            .body(event.copy(name = "BreizhCamp 2025 - Updated"))
            .exchange()
            .expectStatus().isNoContent()

        // Check update
        client.listEvents()
            .jsonPath("$[?(@.id == '$eventId' && @.name == 'BreizhCamp 2025 - Updated')]").exists()

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
        val token = getUserToken()
        val client = webClient.mutate().defaultHeader("Authorization", "Bearer $token").build()
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
            .uri("/events")
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
