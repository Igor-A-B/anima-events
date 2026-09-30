package com.example.anima.features.addevent.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_about_hint
import anima.app.shared.generated.resources.add_event_about_label
import anima.app.shared.generated.resources.add_event_address_hint
import anima.app.shared.generated.resources.add_event_capacity_hint
import anima.app.shared.generated.resources.add_event_category_label
import anima.app.shared.generated.resources.add_event_date_hint
import anima.app.shared.generated.resources.add_event_location_hint
import anima.app.shared.generated.resources.add_event_name_hint
import anima.app.shared.generated.resources.add_event_price_hint
import anima.app.shared.generated.resources.add_event_time_hint
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideBuilding
import com.example.anima.core.components.icon.lucide.LucideMapPin
import com.example.anima.core.components.icon.lucide.LucideTag
import com.example.anima.core.components.icon.lucide.LucideTicket
import com.example.anima.core.components.icon.lucide.LucideUsers
import com.example.anima.core.components.picker.AnimaDatePicker
import com.example.anima.core.components.picker.AnimaTimePicker
import com.example.anima.core.components.textfield.AnimaTextField
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.addevent.domain.model.EventDraft
import com.anima.features.event.models.EventCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddEventForm(
    draft: EventDraft,
    onNameChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onCategorySelected: (EventCategory) -> Unit,
    onPriceChanged: (String) -> Unit,
    onCapacityChanged: (String) -> Unit,
    onAboutChanged: (String) -> Unit,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var locationKeyboardOpen by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()

    val locationFocus = FocusRequester()
    val addressFocus = FocusRequester()
    val priceFocus = FocusRequester()
    val capacityFocus = FocusRequester()
    val aboutFocus = FocusRequester()

    LaunchedEffect(locationKeyboardOpen) {
        if (locationKeyboardOpen) {
            locationFocus.requestFocus()

            locationKeyboardOpen = false
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
    ) {
        EventPhotoPicker(onPickPhoto = onPickPhoto)

        AnimaTextField(
            value = draft.name,
            onValueChange = onNameChanged,
            placeholder = stringResource(Res.string.add_event_name_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { showDatePicker = true }
            ),
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideTag,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = AnimaTheme.spacing.lg,
                )
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
        ) {
            AnimaDatePicker(
                value = draft.date,
                onDateSelected = { date ->
                    onDateChanged(date)
                    coroutineScope.launch {
                        delay(100)
                        showTimePicker = true
                    }
                },
                forceShow = showDatePicker,
                onDismiss = { showDatePicker = false },
                placeholder = stringResource(Res.string.add_event_date_hint),
                modifier = Modifier.weight(1f),
            )

            AnimaTimePicker(
                value = draft.time,
                onTimeSelected = { time ->
                    onTimeChanged(time)
                    locationKeyboardOpen = true
                },
                forceShow = showTimePicker,
                onDismiss = { showTimePicker = false },
                placeholder = stringResource(Res.string.add_event_time_hint),
                modifier = Modifier.weight(1f),
            )
        }

        AnimaTextField(
            value = draft.location,
            onValueChange = onLocationChanged,
            placeholder = stringResource(Res.string.add_event_location_hint),
            modifier = Modifier.focusRequester(locationFocus),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    addressFocus.requestFocus()
                }
            ),
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideMapPin,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = AnimaTheme.spacing.lg,
                )
            },
        )

        AnimaTextField(
            value = draft.address,
            onValueChange = onAddressChanged,
            placeholder = stringResource(Res.string.add_event_address_hint),
            modifier = Modifier.focusRequester(addressFocus),
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideBuilding,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = AnimaTheme.spacing.lg,
                )
            },
        )

        Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
            Text(
                text = stringResource(Res.string.add_event_category_label),
                style = AnimaTheme.typography.bodyLarge,
                color = AnimaTheme.colors.onSurfaceVariant,
            )

            EventCategorySelector(
                selected = draft.category,
                onSelect = { category ->
                    onCategorySelected(category)
                    priceFocus.requestFocus()
                },
            )
        }

        AnimaTextField(
            value = draft.price,
            onValueChange = onPriceChanged,
            placeholder = stringResource(Res.string.add_event_price_hint),
            modifier = Modifier.focusRequester(priceFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    capacityFocus.requestFocus()
                }
            ),
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideTicket,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = AnimaTheme.spacing.lg,
                )
            },
        )

        AnimaTextField(
            value = draft.capacity,
            onValueChange = onCapacityChanged,
            placeholder = stringResource(Res.string.add_event_capacity_hint),
            modifier = Modifier.focusRequester(capacityFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    aboutFocus.requestFocus()
                }
            ),
            leadingIcon = {
                AnimaIcon(
                    imageVector = LucideUsers,
                    contentDescription = null,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    size = AnimaTheme.spacing.lg,
                )
            },
        )

        Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
            Text(
                text = stringResource(Res.string.add_event_about_label),
                style = AnimaTheme.typography.bodyLarge,
                color = AnimaTheme.colors.onSurfaceVariant,
            )

            EventTextArea(
                value = draft.about,
                onValueChange = onAboutChanged,
                placeholder = stringResource(Res.string.add_event_about_hint),
                modifier = Modifier.focusRequester(aboutFocus),
            )
        }

        Spacer(modifier = Modifier.height(AnimaTheme.spacing.lg))
    }
}