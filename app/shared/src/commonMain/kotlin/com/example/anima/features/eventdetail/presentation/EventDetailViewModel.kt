package com.example.anima.features.eventdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.Event
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.core_error_image_unsupported
import anima.app.shared.generated.resources.event_detail_image_added
import com.example.anima.core.error.AppExceptionBus
import com.example.anima.core.error.Severity
import com.example.anima.core.image.PickedImage
import com.example.anima.features.auth.data.SessionRepository
import com.example.anima.features.eventdetail.data.EventImageRepository
import com.example.anima.core.network.ApiException
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
    private val actionError = MutableStateFlow<SubscriptionError?>(null)
    private val uploadingImage = MutableStateFlow(false)

    private val baseState = combine(
        event, subscriptions.subscriptions, session.session, busy, actionError,
    ) { event, subs, session, busy, actionError ->
        val status = event?.let { e -> subs.firstOrNull { it.event.id == e.id }?.status }
        val state = subscriptionUiFor(event, status, session?.accountType, busy)
        val isCurator = event != null && event.curatorId.isNotBlank() && event.curatorId == session?.userId
        EventDetailUiState(event, state, actionError, isCurator = isCurator)
    }

    val uiState: StateFlow<EventDetailUiState> = combine(baseState, uploadingImage) { state, uploading ->
        state.copy(isUploadingImage = uploading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventDetailUiState())

    fun loadEvent(id: String) {
        viewModelScope.launch {
            event.value = runCatching { feedRepository.findById(id) }.getOrNull()
            runCatching { subscriptions.refresh() }
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
                // the global mapping reads 409 as a taken email, here it means the last spot is gone
                if ((e as? ApiException)?.status == 409) {
                    actionError.value = SubscriptionError.EVENT_FULL
                    // the new count turns the button into Full
                    reloadEvent(current.id)
                } else {
                    actionError.value = SubscriptionError.GENERIC
                }
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
                bus.report(e)
            } finally {
                uploadingImage.value = false
            }
        }
    }

    fun onUnsupportedImage() {
        bus.show(Res.string.core_error_image_unsupported, Severity.ERROR)
    }
}
