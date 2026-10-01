package com.example.anima.features.eventdetail.presentation.components

import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.event_detail_participate
import anima.app.shared.generated.resources.event_detail_subscribe_again
import anima.app.shared.generated.resources.event_detail_subscription_attended
import anima.app.shared.generated.resources.event_detail_subscription_confirmed
import anima.app.shared.generated.resources.event_detail_subscription_error
import anima.app.shared.generated.resources.event_detail_subscription_full
import anima.app.shared.generated.resources.event_detail_subscription_full_error
import anima.app.shared.generated.resources.event_detail_subscription_loading
import com.example.anima.features.eventdetail.presentation.SubscriptionError
import com.example.anima.features.eventdetail.presentation.SubscriptionUi
import org.jetbrains.compose.resources.stringResource

// button label for each subscription state, Hidden never reaches the footer
@Composable
fun SubscriptionUi.label(): String = stringResource(
    when (this) {
        SubscriptionUi.Loading -> Res.string.event_detail_subscription_loading
        SubscriptionUi.Confirmed -> Res.string.event_detail_subscription_confirmed
        SubscriptionUi.Cancelled -> Res.string.event_detail_subscribe_again
        SubscriptionUi.Attended -> Res.string.event_detail_subscription_attended
        SubscriptionUi.Full -> Res.string.event_detail_subscription_full
        SubscriptionUi.NotSubscribed, SubscriptionUi.Hidden -> Res.string.event_detail_participate
    },
)

// message above the button after a failed subscribe or cancel
@Composable
fun SubscriptionError.message(): String = stringResource(
    when (this) {
        SubscriptionError.EVENT_FULL -> Res.string.event_detail_subscription_full_error
        SubscriptionError.GENERIC -> Res.string.event_detail_subscription_error
    },
)
