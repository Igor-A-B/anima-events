package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anima.core.components.chip.AnimaChip
import com.example.anima.core.components.chip.AnimaChipDefaults
import com.example.anima.core.components.chip.AnimaChipSize
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.EventCategory
import com.example.anima.features.feed.presentation.components.icon
import com.example.anima.features.feed.presentation.components.label

@Composable
fun EventCategorySelector(
    selected: EventCategory?,
    onSelect: (EventCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        items(EventCategory.entries) { category ->
            AnimaChip(
                text = category.label(),
                onClick = { onSelect(category) },
                selected = selected == category,
                size = AnimaChipSize.COMPACT,
                leadingIcon = {
                    AnimaIcon(
                        imageVector = category.icon,
                        contentDescription = null,
                        size = AnimaChipDefaults.CompactIconSize,
                    )
                },
            )
        }
    }
}