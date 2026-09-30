package com.example.anima.features.addevent.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.add_event_delete
import anima.app.shared.generated.resources.add_event_title
import anima.app.shared.generated.resources.core_button_confirm
import anima.app.shared.generated.resources.core_button_save
import anima.app.shared.generated.resources.core_error_generic
import anima.app.shared.generated.resources.edit_event_title
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.components.button.AnimaButton
import com.example.anima.core.components.button.AnimaIconButton
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideArrowLeft
import com.example.anima.core.components.icon.lucide.LucideTrash2
import com.example.anima.core.image.rememberCameraCapture
import com.example.anima.core.image.rememberImagePicker
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.addevent.presentation.components.AddEventForm
import com.example.anima.features.addevent.presentation.components.DeleteEventDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// creates an event, or edits one when eventId is set
@Composable
fun AddEventScreen(
    eventId: String? = null,
    onNavigateBack: () -> Unit = {},
    onEventSaved: () -> Unit = {},
    onEventDeleted: () -> Unit = {},
    viewModel: AddEventViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pickPhoto = rememberImagePicker(
        onPicked = viewModel::onPhotoPicked,
        onUnsupported = viewModel::onUnsupportedPhoto,
    )
    val takePhoto = rememberCameraCapture(
        onCaptured = viewModel::onPhotoPicked,
        onFailed = viewModel::onUnsupportedPhoto,
    )

    LaunchedEffect(eventId) {
        viewModel.load(eventId)
    }

    AnimaScaffold(insets = AnimaScaffoldInsets.Standalone) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    text = stringResource(
                        if (uiState.isEditing) Res.string.edit_event_title else Res.string.add_event_title
                    ),
                    style = AnimaTheme.typography.titleLarge,
                    color = AnimaTheme.colors.onBackground,
                    modifier = Modifier.weight(1f),
                )

                if (uiState.isEditing && !uiState.isLoading && !uiState.loadFailed) {
                    AnimaIconButton(onClick = viewModel::onDeleteClick) {
                        AnimaIcon(
                            imageVector = LucideTrash2,
                            contentDescription = stringResource(Res.string.add_event_delete),
                            tint = AnimaTheme.colors.error,
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AnimaTheme.colors.primary)
                }

                uiState.loadFailed -> Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(AnimaTheme.spacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.core_error_generic),
                        style = AnimaTheme.typography.bodyMedium,
                        color = AnimaTheme.colors.onSurfaceVariant,
                    )
                }

                // scroll content
                else -> Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = AnimaTheme.spacing.sm),
                ) {
                    AddEventForm(
                        draft = uiState.draft,
                        onNameChanged = viewModel::onNameChanged,
                        onDateChanged = viewModel::onDateChanged,
                        onTimeChanged = viewModel::onTimeChanged,
                        onLocationChanged = viewModel::onLocationChanged,
                        onAddressChanged = viewModel::onAddressChanged,
                        onCityChanged = viewModel::onCityChanged,
                        onCategorySelected = viewModel::onCategorySelected,
                        onPriceChanged = viewModel::onPriceChanged,
                        onCapacityChanged = viewModel::onCapacityChanged,
                        onAboutChanged = viewModel::onAboutChanged,
                        photo = uiState.photo,
                        onPickPhoto = pickPhoto,
                        onTakePhoto = takePhoto,
                        onRemovePhoto = viewModel::onPhotoRemoved,
                        modifier = Modifier.padding(
                            horizontal = AnimaTheme.spacing.xl
                        ),
                    )
                }
            }

            // footer
            AnimaButton(
                text = stringResource(
                    if (uiState.isEditing) Res.string.core_button_save else Res.string.core_button_confirm
                ),
                onClick = {
                    viewModel.onSubmit(onEventSaved)
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

    if (uiState.isDeleteDialogOpen) {
        DeleteEventDialog(
            isDeleting = uiState.isDeleting,
            onConfirm = { viewModel.onConfirmDelete(onEventDeleted) },
            onDismiss = viewModel::onDismissDeleteDialog,
        )
    }
}
