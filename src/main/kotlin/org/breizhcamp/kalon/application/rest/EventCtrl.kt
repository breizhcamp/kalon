package org.breizhcamp.kalon.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.breizhcamp.kalon.application.dto.EventAPI
import org.breizhcamp.kalon.application.dto.EventFullAPI
import org.breizhcamp.kalon.application.dto.toFullApi
import org.breizhcamp.kalon.application.dto.toSummaryApi
import org.breizhcamp.kalon.config.log.KalonMDC
import org.breizhcamp.kalon.config.log.Log
import org.breizhcamp.kalon.config.security.IsAdmin
import org.breizhcamp.kalon.config.security.IsUser
import org.breizhcamp.kalon.domain.entities.Event
import org.breizhcamp.kalon.domain.entities.EventId
import org.breizhcamp.kalon.domain.exceptions.EventIdAlreadyExistsException
import org.breizhcamp.kalon.domain.exceptions.InconsistentStartEndDateException
import org.breizhcamp.kalon.domain.use_cases.EventCRUD
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/events", produces = ["application/json"])
@Tag(name = "events", description = "Event related operations")
class EventCtrl(
    private val eventCRUD: EventCRUD
) {

    @Operation(summary = "Create a new Event")
    @PostMapping
    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Log eventAPI: EventFullAPI) {
        eventCRUD.create(eventAPI.toDomain())
    }

    @Operation(summary = "Update an existing Event")
    @PutMapping("/{id}")
    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable @Log(KalonMDC.EVENT_ID) id: String, @RequestBody @Log eventAPI: EventFullAPI) {
        eventCRUD.update(eventAPI.toDomain(id))
    }

    @Operation(summary = "Delete an existing Event")
    @DeleteMapping("/{id}")
    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable @Log(KalonMDC.EVENT_ID) id: String) {
        eventCRUD.delete(EventId(id))
    }

    @Operation(summary = "List all Events")
    @GetMapping
    @IsUser
    fun list(
        @Parameter(description = "View type: 'summary' for id and name only, 'full' (default) for complete information")
        @RequestParam(defaultValue = "full") view: String
    ): List<EventAPI> {
        val mapper =  when (view) {
            "summary" -> Event::toSummaryApi
            else -> Event::toFullApi
        }
        return eventCRUD.list().map { mapper(it) }
    }

    @Operation(summary = "Get an event")
    @GetMapping("/{id}")
    @IsUser
    fun get(@PathVariable @Log(KalonMDC.EVENT_ID) id: String): EventFullAPI =
        eventCRUD.get(EventId(id)).toFullApi()

    @Operation(summary = "Check if event id is available", responses = [
        ApiResponse(description = "Event id is available", responseCode = "200"),
        ApiResponse(description = "Event id is not available", responseCode = "404")
    ])
    @RequestMapping(method = [RequestMethod.HEAD], value = ["/{id}"])
    @IsUser
    fun exists(@PathVariable @Log(KalonMDC.EVENT_ID) id: String): ResponseEntity<Unit> {
        if (!eventCRUD.exists(EventId(id))) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().build()
    }

    @ExceptionHandler(EventIdAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun idAlreadyExistsExceptionHandler() {}

    @ExceptionHandler(InconsistentStartEndDateException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun inconsistentStartEndDateExceptionHandler() {}
}
