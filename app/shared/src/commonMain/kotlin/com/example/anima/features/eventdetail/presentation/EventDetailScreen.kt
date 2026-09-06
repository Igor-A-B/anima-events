package com.example.anima.features.eventdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.eventdetail.presentation.components.EventAbout
import com.example.anima.features.eventdetail.presentation.components.EventCover
import com.example.anima.features.eventdetail.presentation.components.EventFooter
import com.example.anima.features.eventdetail.presentation.components.EventInfoGrid
import com.example.anima.features.eventdetail.presentation.components.EventOrganizer
import com.example.anima.features.feed.domain.Event

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: EventDetailViewModel = viewModel(),
) {
    val event by viewModel.event.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    event?.let { safeEvent ->
        EventDetailContent(
            event = safeEvent,
            onNavigateBack = onNavigateBack,
            onShare = {},
            onFavorite = {},
            onParticipate = {},
        )
    }
}

@Composable
private fun EventDetailContent(
    event: Event,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    onFavorite: () -> Unit,
    onParticipate: () -> Unit,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.Standalone) {
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = AnimaTheme.spacing.huge,
                ),
            ) {
                item {
                    EventCover(
                        coverSeed = event.coverSeed,
                        title = event.title,
                        category = event.category,
                        price = event.price,
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
                            attendees = event.attendees,
                        )

                        EventOrganizer(
                            organizerName = event.organizerName,
                        )

                        EventAbout(description = event.description)
                    }
                }
            }

            EventFooter(
                onParticipate = onParticipate,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = AnimaTheme.spacing.xl)
                    .padding(bottom = AnimaTheme.spacing.sm),
            )
        }
    }
}