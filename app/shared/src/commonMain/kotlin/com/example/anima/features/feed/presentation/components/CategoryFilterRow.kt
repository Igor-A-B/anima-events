package com.example.anima.features.feed.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.categories_all
import com.example.anima.core.components.chip.AnimaChip
import com.example.anima.core.components.chip.AnimaChipDefaults
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.feed.domain.EventCategory
import org.jetbrains.compose.resources.stringResource

// horizontal filter row, null means every category
@Composable
fun CategoryFilterRow(
    selected: EventCategory?,
    onSelect: (EventCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = AnimaTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
    ) {
        item(key = "all") {
            AnimaChip(
                text = stringResource(Res.string.categories_all),
                onClick = { onSelect(null) },
                selected = selected == null,
            )
        }

        items(items = EventCategory.entries, key = { category -> category.name }) { category ->
            AnimaChip(
                text = category.label(),
                onClick = { onSelect(category) },
                selected = category == selected,
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
