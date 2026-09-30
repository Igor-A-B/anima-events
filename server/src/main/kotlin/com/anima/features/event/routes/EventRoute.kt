package com.anima.features.event.routes

import com.anima.features.event.dtos.CreateEventRequestDto
import com.anima.features.event.dtos.EventPageDto
import com.anima.features.event.dtos.UpdateEventRequestDto
import com.anima.features.event.exceptions.EventForbiddenException
import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.event.models.DateFilter
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventCategory
import com.anima.features.event.models.FeedSectionType
import com.anima.features.event.models.PriceFilter
import com.anima.features.event.services.EventService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/events")
class EventRoute(private val eventService: EventService) {
    @PostMapping
    fun create(principal: Principal, @RequestBody request: CreateEventRequestDto): ResponseEntity<Event> =
        ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(UUID.fromString(principal.name), request))

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Event = eventService.get(id)

    // section is optional, so each feed row can lazy-load itself: /events?section=NEARBY&lat=..&lng=..
    // the search screen leaves it out and narrows with q, category (repeatable), price and date
    @GetMapping
    fun search(
        principal: Principal?,
        @RequestParam(required = false) section: FeedSectionType?,
        @RequestParam(required = false) category: List<EventCategory>?,
        @RequestParam(required = false) q: String?,
        @RequestParam(defaultValue = "ANY") price: PriceFilter,
        @RequestParam(defaultValue = "ANY") date: DateFilter,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lng: Double?,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<EventPageDto> {
        // 401, not 400, so the client refreshes an expired token and retries
        if (section == FeedSectionType.PARTICIPATING && principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return ResponseEntity.ok(
            eventService.search(
                section, category.orEmpty().toSet(), q, price, date, lat, lng, principal?.name?.let(UUID::fromString), cursor, size,
            ),
        )
    }

    @PutMapping("/{id}")
    fun update(principal: Principal, @PathVariable id: UUID, @RequestBody request: UpdateEventRequestDto): Event =
        eventService.update(UUID.fromString(principal.name), id, request)

    @DeleteMapping("/{id}")
    fun delete(principal: Principal, @PathVariable id: UUID): ResponseEntity<Void> {
        eventService.delete(UUID.fromString(principal.name), id)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(EventNotFoundException::class)
    fun handleNotFound(ex: RuntimeException) = error(HttpStatus.NOT_FOUND, ex)

    @ExceptionHandler(EventForbiddenException::class)
    fun handleForbidden(ex: RuntimeException) = error(HttpStatus.FORBIDDEN, ex)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: RuntimeException) = error(HttpStatus.BAD_REQUEST, ex)

    private fun error(status: HttpStatus, ex: RuntimeException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(status).body(mapOf("error" to (ex.message ?: status.name)))
}
