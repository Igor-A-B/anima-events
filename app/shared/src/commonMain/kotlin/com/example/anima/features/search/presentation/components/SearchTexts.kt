package com.example.anima.features.search.presentation.components

import androidx.compose.runtime.Composable
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.search_date_any
import anima.app.shared.generated.resources.search_date_now
import anima.app.shared.generated.resources.search_date_today
import anima.app.shared.generated.resources.search_date_tomorrow
import anima.app.shared.generated.resources.search_date_weekend
import anima.app.shared.generated.resources.search_price_any
import anima.app.shared.generated.resources.search_price_free
import anima.app.shared.generated.resources.search_price_paid
import com.example.anima.features.search.domain.DateFilter
import com.example.anima.features.search.domain.PriceFilter
import org.jetbrains.compose.resources.stringResource

// same idea as FeedTexts: the domain enums stay language free
@Composable
fun PriceFilter.label(): String = stringResource(
    when (this) {
        PriceFilter.ANY -> Res.string.search_price_any
        PriceFilter.FREE -> Res.string.search_price_free
        PriceFilter.PAID -> Res.string.search_price_paid
    }
)

@Composable
fun DateFilter.label(): String = stringResource(
    when (this) {
        DateFilter.ANY -> Res.string.search_date_any
        DateFilter.NOW -> Res.string.search_date_now
        DateFilter.TODAY -> Res.string.search_date_today
        DateFilter.TOMORROW -> Res.string.search_date_tomorrow
        DateFilter.WEEKEND -> Res.string.search_date_weekend
    }
)
