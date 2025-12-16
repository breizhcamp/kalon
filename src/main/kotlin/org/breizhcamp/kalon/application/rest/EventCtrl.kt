package org.breizhcamp.kalon.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.breizhcamp.kalon.application.dto.EventAPI
import org.breizhcamp.kalon.config.log.Log
import org.breizhcamp.kalon.config.security.IsAdmin
import org.breizhcamp.kalon.domain.exceptions.EventIdAlreadyExistsException
import org.breizhcamp.kalon.domain.exceptions.InconsistentStartEndDateException
import org.breizhcamp.kalon.domain.use_cases.EventCRUD
import org.springframework.http.HttpStatus
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
    fun create(@RequestBody @Log eventAPI: EventAPI) {
        eventCRUD.create(eventAPI.toDomain())
    }

    @Operation(summary = "Update an existing Event")
    @PutMapping
    @IsAdmin
    fun update(@RequestBody @Log eventAPI: EventAPI) {
        eventCRUD.update(eventAPI.toDomain())
    }

    @ExceptionHandler(EventIdAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun idAlreadyExistsExceptionHandler() {}

    @ExceptionHandler(InconsistentStartEndDateException::class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    fun inconsistentStartEndDateExceptionHandler() {}
}
