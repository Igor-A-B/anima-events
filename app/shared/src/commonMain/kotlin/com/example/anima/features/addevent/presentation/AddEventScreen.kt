package com.example.anima.features.addevent.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_title
import anima.app.shared.generated.resources.core_button_confirm
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaIconButton
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.addevent.presentation.components.AddEventForm
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddEventScreen(
    onNavigateBack: () -> Unit = {},
    onEventCreated: () -> Unit = {},
    viewModel: AddEventViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimaScaffold(insets = AnimaScaffoldInsets.Standalone) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // scroll content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = AnimaTheme.spacing.sm),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AnimaTheme.spacing.xl,
                            vertical = AnimaTheme.spacing.lg,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        AnimaTheme.spacing.md
                    ),
                ) {
                    AnimaIconButton(onClick = onNavigateBack) {
                        AnimaIcon(
                            imageVector = LucideArrowLeft,
                            contentDescription = null,
                            tint = AnimaTheme.colors.primary,
                        )
                    }

                    Text(
                        text = stringResource(Res.string.add_event_title),
                        style = AnimaTheme.typography.titleLarge,
                        color = AnimaTheme.colors.onBackground,
                    )
                }

                AddEventForm(
                    draft = uiState.draft,
                    onNameChanged = viewModel::onNameChanged,
                    onDateChanged = viewModel::onDateChanged,
                    onTimeChanged = viewModel::onTimeChanged,
                    onLocationChanged = viewModel::onLocationChanged,
                    onAddressChanged = viewModel::onAddressChanged,
                    onCategorySelected = viewModel::onCategorySelected,
                    onPriceChanged = viewModel::onPriceChanged,
                    onCapacityChanged = viewModel::onCapacityChanged,
                    onAboutChanged = viewModel::onAboutChanged,
                    onPickPhoto = {},
                    modifier = Modifier.padding(
                        horizontal = AnimaTheme.spacing.xl
                    ),
                )
            }

            // footer
            AnimaButton(
                text = stringResource(Res.string.core_button_confirm),
                onClick = {
                    viewModel.onSubmit(onEventCreated)
                },
                enabled = uiState.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AnimaTheme.spacing.xl,
                        vertical = AnimaTheme.spacing.sm,
                    )
            )
        }
    }
}