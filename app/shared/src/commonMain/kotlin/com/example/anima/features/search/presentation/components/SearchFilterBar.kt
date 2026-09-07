package com.example.anima.features.search.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.search_filters
import anima.app.shared.generated.resources.search_filters_count
import com.example.anima.core.components.chip.AnimaChip
import com.example.anima.core.components.chip.AnimaChipDefaults
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideSlidersHorizontal
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.presentation.components.icon
import com.example.anima.features.feed.presentation.components.label
import org.jetbrains.compose.resources.stringResource

/** Filters button section plus the category chips. */
@Composable
fun SearchFilterBar(
    selectedCategories: Set<EventCategory>,
    activeFilterCount: Int,
    onToggleCategory: (EventCategory) -> Unit,
    onOpenFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimaChip(
            text = if (activeFilterCount > 0) {
                stringResource(Res.string.search_filters_count, activeFilterCount.toString())
            } else {
                stringResource(Res.string.search_filters)
            },
            onClick = onOpenFilters,
            selected = activeFilterCount > 0,
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideSlidersHorizontal,
                    contentDescription = null,
                    size = AnimaChipDefaults.IconSize,
                )
            },
            modifier = Modifier.padding(start = AnimaTheme.spacing.lg),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = AnimaTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
        ) {
            items(
                items = EventCategory.entries,
                key = { category -> category.name },
            ) { category ->
                AnimaChip(
                    text = category.label(),
                    onClick = { onToggleCategory(category) },
                    selected = category in selectedCategories,
                    leadingIcon = {
                        AnimaIcon(
                            imageVector = category.icon,
                            contentDescription = null,
                            size = AnimaChipDefaults.IconSize,
                        )
                    },
                )
            }
        }
    }
}
