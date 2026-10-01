package com.example.anima.features.eventdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import com.example.anima.core.image.rememberImagePicker
import com.example.anima.core.components.AnimaPullToRefresh
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.anima.features.event.models.Event
import com.example.anima.features.eventdetail.presentation.components.EventAbout
import com.example.anima.features.eventdetail.presentation.components.EventCover
import com.example.anima.features.eventdetail.presentation.components.EventFooter
import com.example.anima.core.error.AppError
import com.example.anima.features.eventdetail.presentation.components.EventInfoGrid
import com.example.anima.features.eventdetail.presentation.components.EventOrganizer
import com.example.anima.features.feed.presentation.components.coverImageUrl

private val FOOTER_RESERVE = 112.dp

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: EventDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val pickImage = rememberImagePicker(
        onPicked = viewModel::onImagePicked,
        onUnsupported = viewModel::onUnsupportedImage,
    )

    uiState.event?.let { safeEvent ->
        EventDetailContent(
            event = safeEvent,
            subscription = uiState.subscription,
            actionError = uiState.actionError,
            isCurator = uiState.isCurator,
            isUploadingImage = uiState.isUploadingImage,
            isRefreshing = uiState.isRefreshing,
            onAddImage = pickImage,
            onNavigateBack = onNavigateBack,
            onShare = {},
            onFavorite = {},
            onSubscribe = viewModel::onSubscribeClick,
            onRefresh = viewModel::refresh,
        )
    }
}

@Composable
private fun EventDetailContent(
    event: Event,
    subscription: SubscriptionUi,
    actionError: AppError?,
    isCurator: Boolean,
    isUploadingImage: Boolean,
    isRefreshing: Boolean,
    onAddImage: () -> Unit,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    onSubscribe: () -> Unit,
    onRefresh: () -> Unit,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.Standalone) {
        // the footer floats in the same box, only the list feeds the pull gesture
        AnimaPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // room for the floating footer
                contentPadding = PaddingValues(
                    bottom = if (subscription == SubscriptionUi.Hidden) AnimaTheme.spacing.huge else FOOTER_RESERVE,
                ),
            ) {
                item {
                    EventCover(
                        coverSeed = event.coverSeed,
                        title = event.title,
                        category = event.category,
                        price = event.price,
                        imageUrl = event.coverImageUrl(),
                        canAddImage = isCurator,
                        isUploadingImage = isUploadingImage,
                        onAddImage = onAddImage,
                        onBack = onNavigateBack,
                        onShare = onShare,
                        onFavorite = onFavorite
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AnimaTheme.colors.background)
                            .padding(horizontal = AnimaTheme.spacing.xl)
                            .padding(top = AnimaTheme.spacing.xl),
                        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
                    ) {
                        EventInfoGrid(
                            dateLabel = event.dateLabel,
                            timeLabel = event.timeLabel,
                            venue = event.venue,
                            city = event.city,
                            distanceLabel = event.distanceLabel,
                            event = event,
                        )

                        EventOrganizer(
                            organizerName = event.organizerName,
                        )

                        EventAbout(description = event.description)
                    }
                }
            }

            if (subscription != SubscriptionUi.Hidden) {
                EventFooter(
                    state = subscription,
                    error = actionError,
                    onClick = onSubscribe,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = AnimaTheme.spacing.xl)
                        .padding(bottom = AnimaTheme.spacing.sm),
                )
            }
        }
    }
}