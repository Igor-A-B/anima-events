package com.example.anima.features.profile.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.profile_change
import anima.app.shared.generated.resources.profile_field_email
import anima.app.shared.generated.resources.profile_field_name
import anima.app.shared.generated.resources.profile_field_password
import anima.app.shared.generated.resources.profile_logout
import anima.app.shared.generated.resources.profile_password_mask
import anima.app.shared.generated.resources.profile_event_going
import anima.app.shared.generated.resources.profile_event_published
import anima.app.shared.generated.resources.profile_events_empty
import anima.app.shared.generated.resources.profile_events_empty_visitor
import anima.app.shared.generated.resources.profile_events_retry
import anima.app.shared.generated.resources.profile_section_account
import anima.app.shared.generated.resources.profile_section_events
import anima.app.shared.generated.resources.profile_section_security
import anima.app.shared.generated.resources.profile_title
// TODO implement? these belong to the sections commented out below
//import anima.app.shared.generated.resources.profile_contact_hint
//import anima.app.shared.generated.resources.profile_delete_account
//import anima.app.shared.generated.resources.profile_events_count
//import anima.app.shared.generated.resources.profile_field_contact_link
//import anima.app.shared.generated.resources.profile_field_phone
//import anima.app.shared.generated.resources.profile_field_recovery_email
//import anima.app.shared.generated.resources.profile_hide
//import anima.app.shared.generated.resources.profile_reveal
//import anima.app.shared.generated.resources.profile_section_contact
//import anima.app.shared.generated.resources.profile_see_all
import com.example.anima.core.components.AnimaScaffold
import com.example.anima.core.error.AppError
import com.example.anima.core.error.messageRes
import com.example.anima.core.components.AnimaScaffoldInsets
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideLogOut
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.profile.domain.model.UserProfile
import com.example.anima.features.profile.presentation.components.ChangePasswordDialog
import com.anima.features.event.models.Event
import com.example.anima.features.profile.presentation.components.ProfileDivider
import com.example.anima.features.profile.presentation.components.ProfileEventRow
import com.example.anima.features.profile.presentation.components.ProfileHeader
import com.example.anima.features.profile.presentation.components.ProfileRow
import com.example.anima.features.profile.presentation.components.ProfileSection
// TODO implement?
//import com.example.anima.core.components.icon.lucide.LucideEye
//import com.example.anima.core.components.icon.lucide.LucideEyeOff
//import com.example.anima.core.components.icon.lucide.LucideTrash2
//import com.example.anima.features.profile.presentation.components.VerificationCard
//import com.example.anima.features.profile.presentation.components.documentLabel
//import com.example.anima.features.profile.presentation.components.maskDocument
import org.jetbrains.compose.resources.stringResource
import com.example.anima.navigation.bottomnav.AnimaBottomNavDefaults

// profile entry point, called by appNavGraph
@Composable
fun ProfileScreen(
    // called after the session ends, by logout or by a password change
    onSignedOut: () -> Unit = {},
    // exhibitor only, opens one of their events in the edit form
    onEditEvent: (String) -> Unit = {},
    // visitor only, opens the detail of an event they take part in
    onOpenEvent: (String) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // the events change on the server too, so the section refreshes when the screen returns
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onResume() }

    ProfileContent(
        uiState = uiState,
        // TODO: no profile photo endpoint yet; when it exists, launch rememberImagePicker (core/image) here
        //  and send the PickedImage with ImageUploader.upload("<endpoint>", image) from the view model
        onPickPhoto = {},
        onChangeName = { /* TODO: edit sheet comes with the validations */ },
        onChangePassword = viewModel::onOpenPasswordDialog,
        onLogout = { viewModel.onLogout(onSignedOut) },
        onEventClick = { event -> if (uiState.isExhibitor) onEditEvent(event.id) else onOpenEvent(event.id) },
        onRetryEvents = viewModel::loadEvents,
        // TODO implement?
        // onToggleDocument = viewModel::onToggleDocumentVisibility,
        // onSeeAllEvents = viewModel::onSeeAllEvents,
        // onValidateAccount = { /* TODO: verification flow not planned yet */ },
        // onDeleteAccount = { /* TODO: needs a confirmation dialog */ },
    )

    if (uiState.isPasswordDialogOpen) {
        ChangePasswordDialog(
            isSaving = uiState.isChangingPassword,
            error = uiState.passwordError,
            onSave = { current, new -> viewModel.onChangePassword(current, new, onSignedOut) },
            onDismiss = viewModel::onDismissPasswordDialog,
        )
    }
}

// stateless part: data in, lambdas out
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onPickPhoto: () -> Unit,
    onChangeName: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onEventClick: (Event) -> Unit,
    onRetryEvents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimaScaffold(insets = AnimaScaffoldInsets.WithChrome) {
        Box(modifier = modifier.fillMaxSize()) {
            val profile = uiState.profile

            when {
                uiState.isLoading -> CircularProgressIndicator(
                    color = AnimaTheme.colors.primary,
                    modifier = Modifier.align(Alignment.Center),
                )

                uiState.error != null || profile == null -> Text(
                    text = stringResource((uiState.error ?: AppError.UNKNOWN).messageRes()),
                    style = AnimaTheme.typography.bodyMedium,
                    color = AnimaTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(AnimaTheme.spacing.xl),
                )

                else -> ProfileList(
                    profile = profile,
                    uiState = uiState,
                    onPickPhoto = onPickPhoto,
                    onChangeName = onChangeName,
                    onChangePassword = onChangePassword,
                    onLogout = onLogout,
                    onEventClick = onEventClick,
                    onRetryEvents = onRetryEvents,
                )
            }
        }
    }
}

@Composable
private fun ProfileList(
    profile: UserProfile,
    uiState: ProfileUiState,
    onPickPhoto: () -> Unit,
    onChangeName: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onEventClick: (Event) -> Unit,
    onRetryEvents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val change = stringResource(Res.string.profile_change)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = AnimaTheme.spacing.lg,
            end = AnimaTheme.spacing.lg,
            top = AnimaTheme.spacing.lg,
            bottom = AnimaBottomNavDefaults.ContentReserve,
        ),
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xl),
    ) {
        item(key = "title") {
            Text(
                text = stringResource(Res.string.profile_title),
                style = AnimaTheme.typography.headlineSmall,
                color = AnimaTheme.colors.onBackground,
            )
        }

        item(key = "header") {
            ProfileHeader(profile = profile, accountType = uiState.accountType, onPickPhoto = onPickPhoto)
        }

        // TODO implement?
        // an exhibitor that is not verified yet gets the call to action on top
        // if (uiState.isExhibitor && !profile.isVerified) {
        //     item(key = "verification") {
        //         VerificationCard(onValidate = onValidateAccount)
        //     }
        // }

        item(key = "account") {
            ProfileSection(title = stringResource(Res.string.profile_section_account)) {
                ProfileRow(
                    label = stringResource(Res.string.profile_field_name),
                    value = profile.name,
                    actionText = change,
                    onActionClick = onChangeName,
                )
                ProfileDivider()
                ProfileRow(
                    label = stringResource(Res.string.profile_field_email),
                    value = profile.email,
                )
                // TODO implement?
                // ProfileDivider()
                // ProfileRow(
                //     label = uiState.accountType.documentLabel(),
                //     value = if (uiState.isDocumentVisible) {
                //         profile.document
                //     } else {
                //         maskDocument(profile.document)
                //     },
                //     trailing = {
                //         Box(modifier = Modifier.clickable(onClick = onToggleDocument)) {
                //             AnimaIcon(
                //                 imageVector = if (uiState.isDocumentVisible) LucideEyeOff else LucideEye,
                //                 contentDescription = stringResource(
                //                     if (uiState.isDocumentVisible) Res.string.profile_hide
                //                     else Res.string.profile_reveal
                //                 ),
                //                 tint = AnimaTheme.colors.onSurfaceVariant,
                //                 size = 20.dp,
                //             )
                //         }
                //     },
                // )
            }
        }

        item(key = "security") {
            ProfileSection(title = stringResource(Res.string.profile_section_security)) {
                ProfileRow(
                    label = stringResource(Res.string.profile_field_password),
                    value = stringResource(Res.string.profile_password_mask),
                    actionText = change,
                    onActionClick = onChangePassword,
                )
                // TODO implement?
                // ProfileDivider()
                // ProfileRow(
                //     label = stringResource(Res.string.profile_field_recovery_email),
                //     value = profile.recoveryEmail,
                //     actionText = change,
                //     onActionClick = onChangeField,
                // )
            }
        }

        // TODO implement?
        // contact and created events belong to the exhibitor only
        // profile.contact?.let { contact ->
        //     item(key = "contact") {
        //         Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.sm)) {
        //             ProfileSection(title = stringResource(Res.string.profile_section_contact)) {
        //                 ProfileRow(
        //                     label = stringResource(Res.string.profile_field_contact_link),
        //                     value = contact.link,
        //                     actionText = change,
        //                     onActionClick = onChangeField,
        //                 )
        //                 ProfileDivider()
        //                 ProfileRow(
        //                     label = stringResource(Res.string.profile_field_phone),
        //                     value = contact.phone,
        //                     actionText = change,
        //                     onActionClick = onChangeField,
        //                 )
        //             }
        //
        //             Text(
        //                 text = stringResource(Res.string.profile_contact_hint),
        //                 style = AnimaTheme.typography.labelSmall,
        //                 color = AnimaTheme.colors.outline,
        //                 modifier = Modifier.padding(start = AnimaTheme.spacing.xs),
        //             )
        //         }
        //     }
        // }

        // an exhibitor taps an event to edit or delete it, a visitor taps it to see the details
        item(key = "events") {
            ProfileSection(title = stringResource(Res.string.profile_section_events)) {
                Column(modifier = Modifier.padding(AnimaTheme.spacing.sm)) {
                    when {
                        uiState.myEvents.isNotEmpty() -> uiState.myEvents.forEach { event ->
                            ProfileEventRow(
                                event = event,
                                onClick = { onEventClick(event) },
                                upcomingLabel = stringResource(
                                    if (uiState.isExhibitor) Res.string.profile_event_published
                                    else Res.string.profile_event_going
                                ),
                            )
                        }

                        uiState.isLoadingEvents -> CircularProgressIndicator(
                            color = AnimaTheme.colors.primary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(AnimaTheme.spacing.sm)
                                .size(20.dp),
                        )

                        uiState.eventsError != null -> Text(
                            // the retry text already says the load failed, a specific reason goes above it
                            text = listOfNotNull(
                                uiState.eventsError.takeIf { it != AppError.UNKNOWN }?.let { stringResource(it.messageRes()) },
                                stringResource(Res.string.profile_events_retry),
                            ).joinToString("\n"),
                            style = AnimaTheme.typography.bodyMedium,
                            color = AnimaTheme.colors.onSurfaceVariant,
                            modifier = Modifier
                                .clickable(onClick = onRetryEvents)
                                .padding(AnimaTheme.spacing.sm),
                        )

                        else -> Text(
                            text = stringResource(
                                if (uiState.isExhibitor) Res.string.profile_events_empty
                                else Res.string.profile_events_empty_visitor
                            ),
                            style = AnimaTheme.typography.bodyMedium,
                            color = AnimaTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(AnimaTheme.spacing.sm),
                        )
                    }
                }
            }
        }

        item(key = "session") {
            Column(verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.lg)) {
                LogoutButton(onClick = onLogout, enabled = !uiState.isSigningOut)
                // TODO implement?
                // DeleteAccountAction(onClick = onDeleteAccount)
            }
        }
    }
}

// TODO implement?
//@Composable
//private fun EventsTrailing(
//    shown: Int,
//    total: Int,
//    isLoading: Boolean,
//    onSeeAll: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Text(
//            text = stringResource(
//                Res.string.profile_events_count,
//                shown.toString(),
//                total.toString(),
//            ),
//            style = AnimaTheme.typography.labelSmall,
//            color = AnimaTheme.colors.outline,
//        )
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                color = AnimaTheme.colors.primary,
//                strokeWidth = 2.dp,
//                modifier = Modifier.size(16.dp),
//            )
//        } else {
//            Text(
//                text = stringResource(Res.string.profile_see_all),
//                style = AnimaTheme.typography.labelLarge,
//                color = AnimaTheme.colors.primaryVariant,
//                modifier = Modifier
//                    .clip(AnimaTheme.shapes.full)
//                    .clickable(onClick = onSeeAll)
//                    .padding(
//                        horizontal = AnimaTheme.spacing.sm,
//                        vertical = AnimaTheme.spacing.xs,
//                    ),
//            )
//        }
//    }
//}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AnimaTheme.shapes.full)
            .border(
                width = 1.dp,
                color = AnimaTheme.colors.outline,
                shape = AnimaTheme.shapes.full,
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = AnimaTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(
            space = AnimaTheme.spacing.sm,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimaIcon(
            imageVector = LucideLogOut,
            contentDescription = null,
            tint = AnimaTheme.colors.onSurface,
            size = 18.dp,
        )

        Text(
            text = stringResource(Res.string.profile_logout),
            style = AnimaTheme.typography.titleMedium,
            color = AnimaTheme.colors.onSurface,
        )
    }
}

// TODO implement?
//@Composable
//private fun DeleteAccountAction(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(AnimaTheme.shapes.full)
//            .clickable(onClick = onClick)
//            .padding(AnimaTheme.spacing.sm),
//        horizontalArrangement = Arrangement.spacedBy(
//            space = AnimaTheme.spacing.sm,
//            alignment = Alignment.CenterHorizontally,
//        ),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        AnimaIcon(
//            imageVector = LucideTrash2,
//            contentDescription = null,
//            tint = AnimaTheme.colors.error,
//            size = 16.dp,
//        )
//
//        Text(
//            text = stringResource(Res.string.profile_delete_account),
//            style = AnimaTheme.typography.labelLarge,
//            color = AnimaTheme.colors.error,
//        )
//    }
//}
