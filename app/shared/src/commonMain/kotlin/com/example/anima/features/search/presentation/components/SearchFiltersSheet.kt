package com.example.anima.features.search.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.search_filters_apply
import anima.app.shared.generated.resources.search_filters_clear
import anima.app.shared.generated.resources.search_filters_date
import anima.app.shared.generated.resources.search_filters_price
import anima.app.shared.generated.resources.search_filters_title
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.chip.AnimaChip
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.search.domain.DateFilter
import com.example.anima.features.search.domain.PriceFilter
import com.example.anima.features.search.domain.SearchFilters
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/** Advanced filters */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFiltersSheet(
    filters: SearchFilters,
    resultCount: Int,
    onPriceChange: (PriceFilter) -> Unit,
    onDateChange: (DateFilter) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    fun dismissAnimated() {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AnimaTheme.colors.surface,
        contentColor = AnimaTheme.colors.onSurface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = AnimaTheme.colors.onSurfaceVariant)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = AnimaTheme.spacing.lg)
                .padding(bottom = AnimaTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.search_filters_title),
                    style = AnimaTheme.typography.titleLarge,
                    color = AnimaTheme.colors.onSurface,
                    modifier = Modifier.weight(1f),
                )

                if (!filters.isDefault) {
                    Text(
                        text = stringResource(Res.string.search_filters_clear),
                        style = AnimaTheme.typography.labelLarge,
                        color = AnimaTheme.colors.primary,
                        modifier = Modifier
                            .clip(AnimaTheme.shapes.full)
                            .clickable(onClick = onClearFilters)
                            .padding(
                                horizontal = AnimaTheme.spacing.sm,
                                vertical = AnimaTheme.spacing.xs,
                            ),
                    )
                }
            }

            FilterSection(title = stringResource(Res.string.search_filters_price)) {
                PriceFilter.entries.forEach { option ->
                    AnimaChip(
                        text = option.label(),
                        onClick = { onPriceChange(option) },
                        selected = option == filters.price,
                    )
                }
            }

            FilterSection(title = stringResource(Res.string.search_filters_date)) {
                DateFilter.entries.forEach { option ->
                    AnimaChip(
                        text = option.label(),
                        onClick = { onDateChange(option) },
                        selected = option == filters.date,
                    )
                }
            }

            AnimaButton(
                text = stringResource(Res.string.search_filters_apply, resultCount.toString()),
                onClick = { dismissAnimated() },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        Text(
            text = title,
            style = AnimaTheme.typography.titleSmall,
            color = AnimaTheme.colors.onSurfaceVariant,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
        ) {
            content()
        }
    }
}
