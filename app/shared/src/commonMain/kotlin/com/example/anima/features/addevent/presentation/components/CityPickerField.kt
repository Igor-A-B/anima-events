package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_city_hint
import anima.app.shared.generated.resources.add_event_city_no_results
import com.example.anima.core.components.button.AnimaIconButton
import com.example.anima.core.components.chip.AnimaChip
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideGlobe
import com.example.anima.core.components.icon.lucide.LucideX
import com.example.anima.core.components.textfield.AnimaTextField
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.addevent.domain.model.CitySuggestion
import org.jetbrains.compose.resources.stringResource

/**
 * City picker for the event form.
 *
 * Backed by [com.example.anima.features.addevent.data.MockCityCatalog] for now —
 * a fixed local list instead of a real city-search API — see the note on
 * [CitySuggestion]. This composable only knows about that type, so swapping the
 * data source later needs no change here.
 *
 * Once a city is picked, the query field is replaced by a chip: there is nothing
 * left to type, and the "x" is the one way to change the choice.
 */
@Composable
fun CityPickerField(
    selectedCity: CitySuggestion?,
    query: String,
    suggestions: List<CitySuggestion>,
    onQueryChange: (String) -> Unit,
    onSelect: (CitySuggestion) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (selectedCity != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm),
            ) {
                AnimaChip(
                    text = selectedCity.displayName,
                    onClick = onClear,
                    selected = true,
                    leadingIcon = {
                        AnimaIcon(
                            imageVector = LucideGlobe,
                            contentDescription = null,
                            size = AnimaTheme.spacing.lg,
                        )
                    },
                )

                AnimaIconButton(onClick = onClear) {
                    AnimaIcon(
                        imageVector = LucideX,
                        contentDescription = null,
                        tint = AnimaTheme.colors.onSurfaceVariant,
                        size = AnimaTheme.spacing.lg,
                    )
                }
            }
        } else {
            AnimaTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = stringResource(Res.string.add_event_city_hint),
                leadingIcon = {
                    AnimaIcon(
                        imageVector = LucideGlobe,
                        contentDescription = null,
                        tint = AnimaTheme.colors.onSurfaceVariant,
                        size = AnimaTheme.spacing.lg,
                    )
                },
            )

            if (query.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AnimaTheme.spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xxs),
                ) {
                    if (suggestions.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.add_event_city_no_results),
                            style = AnimaTheme.typography.bodySmall,
                            color = AnimaTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = AnimaTheme.spacing.sm),
                        )
                    } else {
                        suggestions.forEach { suggestion ->
                            Text(
                                text = "${suggestion.displayName} · ${suggestion.countryCode}",
                                style = AnimaTheme.typography.bodyMedium,
                                color = AnimaTheme.colors.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(suggestion) }
                                    .padding(vertical = AnimaTheme.spacing.sm),
                            )
                        }
                    }
                }
            }
        }
    }
}
