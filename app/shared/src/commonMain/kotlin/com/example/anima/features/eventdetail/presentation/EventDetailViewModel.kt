package com.example.anima.features.eventdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.Event
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_image_unsupported
import anima.app.shared.generated.resources.event_detail_image_added
import com.example.anima.core.error.AppError
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.error.ErrorContext
import com.example.anima.core.error.toAppError
import com.example.anima.core.error.Severity
import com.example.anima.core.image.PickedImage
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.eventdetail.data.EventImageRepository
import com.example.anima.features.feed.data.FeedRepository
import com.example.anima.features.subscription.data.SubscriptionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val feedRepository: FeedRepository,
    private val subscriptions: SubscriptionRepository,
    session: SessionRepository,
    private val images: EventImageRepository,
    private val bus: AppExceptionBus,
) : ViewModel() {

    private val event = MutableStateFlow<Event?>(null)
    private val busy = MutableStateFlow(false)
    private val actionError = MutableStateFlow<AppError?>(null)
    private val uploadingImage = MutableStateFlow(false)
    private val refreshing = MutableStateFlow(false)
    // the screen passes the id once, refresh reloads the same event
    private var eventId: String? = null

    private val baseState = combine(
        event, subscriptions.subscriptions, session.session, busy, actionError,
    ) { event, subs, session, busy, actionError ->
        val status = event?.let { e -> subs.firstOrNull { it.event.id == e.id }?.status }
        val state = subscriptionUiFor(event, status, session?.accountType, busy)
        val isCurator = event != null && event.curatorId.isNotBlank() && event.curatorId == session?.userId
        EventDetailUiState(event, state, actionError, isCurator = isCurator)
    }

    val uiState: StateFlow<EventDetailUiState> = combine(baseState, uploadingImage, refreshing) { state, uploading, refreshing ->
        state.copy(isUploadingImage = uploading, isRefreshing = refreshing)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventDetailUiState())

    fun loadEvent(id: String) {
        eventId = id
        viewModelScope.launch {
            event.value = runCatching { feedRepository.findById(id) }
                .onFailure { if (it is CancellationException) throw it else bus.report(it, ErrorContext.LOAD_EVENT) }
                .getOrNull()
            runCatching { subscriptions.refresh() }
        }
    }

    // pull to refresh: a failure keeps the event already shown
    fun refresh() {
        val id = eventId ?: return
        if (refreshing.value) return

        refreshing.value = true
        viewModelScope.launch {
            try {
                runCatching { feedRepository.findById(id) }
                    .onFailure { if (it is CancellationException) throw it }
                    .getOrNull()
                    // a load of another id may have started meanwhile
                    ?.takeIf { eventId == id }
                    ?.let { event.value = it }
                runCatching { subscriptions.refresh() }
                    .onFailure { if (it is CancellationException) throw it }
            } finally {
                refreshing.value = false
            }
        }
    }

    fun onSubscribeClick() {
        val current = event.value ?: return
        val subscribe = when (uiState.value.subscription) {
            SubscriptionUi.NotSubscribed, SubscriptionUi.Cancelled -> true
            SubscriptionUi.Confirmed -> false
            else -> return
        }

        viewModelScope.launch {
            busy.value = true
            actionError.value = null
            try {
                if (subscribe) subscriptions.subscribe(current.id) else subscriptions.cancel(current.id)
                // the attendees count changed on the server
                reloadEvent(current.id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val error = e.toAppError(if (subscribe) ErrorContext.SUBSCRIBE else ErrorContext.UNSUBSCRIBE)
                actionError.value = error
                // someone took the last spot first, the new count turns the button into Full
                if (error == AppError.EVENT_FULL) reloadEvent(current.id)
            } finally {
                busy.value = false
            }
        }
    }

    private suspend fun reloadEvent(id: String) {
        try {
            feedRepository.findById(id)?.let { event.value = it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // the previous values stay on screen
        }
    }

    fun onImagePicked(image: PickedImage) {
        val current = event.value ?: return
        if (uploadingImage.value) return

        viewModelScope.launch {
            uploadingImage.value = true
            try {
                images.upload(current.id, image)
                // reload so imageUrls has the new one, the upload already worked if this fails
                runCatching { feedRepository.findById(current.id) }.getOrNull()?.let { event.value = it }
                bus.show(Res.string.event_detail_image_added, Severity.SUCCESS)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                bus.report(e, ErrorContext.UPLOAD_IMAGE)
            } finally {
                uploadingImage.value = false
            }
        }
    }

    fun onUnsupportedImage() {
        bus.show(Res.string.core_error_image_unsupported, Severity.ERROR)
    }
}
