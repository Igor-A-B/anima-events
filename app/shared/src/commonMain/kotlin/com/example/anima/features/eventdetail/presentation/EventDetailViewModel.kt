package com.example.anima.features.eventdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anima.features.event.models.Event
import com.anima.features.event.models.EventStatus
import com.anima.features.subscription.models.SubscriptionStatus
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
import com.anima.features.user.models.AccountType
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

    private val baseState = combine(
        event, subscriptions.subscriptions, session.session, busy, actionError,
    ) { event, subs, session, busy, actionError ->
        val state = when {
            event == null || session?.accountType != AccountType.VISITOR -> SubscriptionUi.Hidden
            busy -> SubscriptionUi.Loading
            else -> {
                val finished = event.status == EventStatus.FINISHED
                when (subs.firstOrNull { it.event.id == event.id }?.status) {
                    SubscriptionStatus.CONFIRMED -> SubscriptionUi.Confirmed
                    SubscriptionStatus.ATTENDED -> SubscriptionUi.Attended
                    SubscriptionStatus.CANCELLED -> if (finished) SubscriptionUi.Hidden else SubscriptionUi.Cancelled
                    null -> if (finished) SubscriptionUi.Hidden else SubscriptionUi.NotSubscribed
                }
            }
        }
        val isCurator = event != null && event.curatorId.isNotBlank() && event.curatorId == session?.userId
        EventDetailUiState(event, state, actionError, isCurator = isCurator)
    }

    val uiState: StateFlow<EventDetailUiState> = combine(baseState, uploadingImage) { state, uploading ->
        state.copy(isUploadingImage = uploading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventDetailUiState())

    fun loadEvent(id: String) {
        viewModelScope.launch {
            event.value = runCatching { feedRepository.findById(id) }
                .onFailure { if (it is CancellationException) throw it else bus.report(it, ErrorContext.LOAD_EVENT) }
                .getOrNull()
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
                runCatching { feedRepository.findById(current.id) }.getOrNull()?.let { event.value = it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                actionError.value = e.toAppError(if (subscribe) ErrorContext.SUBSCRIBE else ErrorContext.UNSUBSCRIBE)
            } finally {
                busy.value = false
            }
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
