package com.anima.features.subscription.routes

import com.anima.features.event.exceptions.EventNotFoundException
import com.anima.features.subscription.exceptions.EventAlreadyFinishedException
import com.anima.features.subscription.exceptions.NotAVisitorException
import com.anima.features.subscription.exceptions.SubscriptionNotCancellableException
import com.anima.features.subscription.models.Subscription
import com.anima.features.subscription.services.SubscriptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@RestController
class SubscriptionRoute(private val subscriptionService: SubscriptionService) {
    @PostMapping("/events/{id}/subscription")
    fun subscribe(principal: Principal, @PathVariable id: UUID): Subscription =
        subscriptionService.subscribe(UUID.fromString(principal.name), id)

    @DeleteMapping("/events/{id}/subscription")
    fun cancel(principal: Principal, @PathVariable id: UUID): Subscription =
        subscriptionService.cancel(UUID.fromString(principal.name), id)

    @GetMapping("/subscriptions/me")
    fun mine(principal: Principal): List<Subscription> =
        subscriptionService.listMine(UUID.fromString(principal.name))

    @ExceptionHandler(EventNotFoundException::class)
    fun handleNotFound(ex: RuntimeException) = error(HttpStatus.NOT_FOUND, ex)

    @ExceptionHandler(NotAVisitorException::class)
    fun handleForbidden(ex: RuntimeException) = error(HttpStatus.FORBIDDEN, ex)

    @ExceptionHandler(EventAlreadyFinishedException::class, SubscriptionNotCancellableException::class)
    fun handleBadRequest(ex: RuntimeException) = error(HttpStatus.BAD_REQUEST, ex)

    private fun error(status: HttpStatus, ex: RuntimeException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(status).body(mapOf("error" to (ex.message ?: status.name)))
}
