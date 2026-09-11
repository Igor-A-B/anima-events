package com.example.anima.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.profile_add_photo
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideCirclePlus
import com.example.anima.core.components.icon.lucide.LucideCircleUser
import com.example.anima.core.components.icon.lucide.LucideStore
import com.example.anima.core.components.icon.lucide.LucideUser
import com.example.anima.core.theme.AnimaTheme
import com.example.anima.features.profile.domain.model.UserProfile
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileHeader(
    profile: UserProfile,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.md),
    ) {
        AvatarPlaceholder(onPickPhoto = onPickPhoto)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        ) {
            Text(
                text = profile.name,
                style = AnimaTheme.typography.titleLarge,
                color = AnimaTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Text(
                text = profile.email,
                style = AnimaTheme.typography.bodyMedium,
                color = AnimaTheme.colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        AccountTypePill(profile = profile)
    }
}

@Composable
private fun AvatarPlaceholder(
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(ProfileHeaderDefaults.AvatarSize)) {
        Box(
            modifier = Modifier
                .size(ProfileHeaderDefaults.AvatarSize)
                .clip(AnimaTheme.shapes.full)
                .background(AnimaTheme.colors.surface)
                .border(
                    width = 1.dp,
                    color = AnimaTheme.colors.outline,
                    shape = AnimaTheme.shapes.full,
                ),
            contentAlignment = Alignment.Center,
        ) {
            // TODO: swap for the uploaded photo once image upload exists
            AnimaIcon(
                imageVector = LucideCircleUser,
                contentDescription = null,
                tint = AnimaTheme.colors.outline,
                size = ProfileHeaderDefaults.AvatarIconSize,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(ProfileHeaderDefaults.BadgeSize)
                .clip(AnimaTheme.shapes.full)
                .background(AnimaTheme.colors.primary)
                .clickable(onClick = onPickPhoto),
            contentAlignment = Alignment.Center,
        ) {
            AnimaIcon(
                imageVector = LucideCirclePlus,
                contentDescription = stringResource(Res.string.profile_add_photo),
                tint = AnimaTheme.colors.onPrimary,
                size = ProfileHeaderDefaults.BadgeIconSize,
            )
        }
    }
}

@Composable
private fun AccountTypePill(
    profile: UserProfile,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(AnimaTheme.shapes.full)
            .background(AnimaTheme.colors.surfaceVariant)
            .padding(
                horizontal = AnimaTheme.spacing.sm,
                vertical = AnimaTheme.spacing.xs,
            ),
        horizontalArrangement = Arrangement.spacedBy(AnimaTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimaIcon(
            imageVector = if (profile.isExhibitor) LucideStore else LucideUser,
            contentDescription = null,
            tint = AnimaTheme.colors.primaryVariant,
            size = ProfileHeaderDefaults.PillIconSize,
        )

        Text(
            text = profile.accountType.label().uppercase(),
            style = AnimaTheme.typography.labelSmall,
            color = AnimaTheme.colors.primaryVariant,
        )
    }
}

object ProfileHeaderDefaults {
    val AvatarSize = 96.dp
    val AvatarIconSize = 44.dp
    val BadgeSize = 30.dp
    val BadgeIconSize = 16.dp
    val PillIconSize = 12.dp
}
