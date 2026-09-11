package com.example.anima.features.search.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import anima.app.shared.generated.resources.Res
import anima.app.shared.generated.resources.search_clear
import anima.app.shared.generated.resources.search_placeholder
import com.example.anima.core.components.form.AnimaTextField
import com.example.anima.core.components.icon.AnimaIcon
import com.example.anima.core.components.icon.lucide.LucideSearch
import com.example.anima.core.components.icon.lucide.LucideX
import com.example.anima.core.theme.AnimaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    AnimaTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = stringResource(Res.string.search_placeholder),
        leadingIcon = {
            AnimaIcon(
                imageVector = LucideSearch,
                contentDescription = null,
                size = SearchFieldDefaults.IconSize,
                tint = AnimaTheme.colors.onSurfaceVariant,
            )
        },
        trailingIcon = if (query.isEmpty()) null else {
            {
                AnimaIcon(
                    imageVector = LucideX,
                    contentDescription = stringResource(Res.string.search_clear),
                    size = SearchFieldDefaults.IconSize,
                    tint = AnimaTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .clip(AnimaTheme.shapes.full)
                        .clickable {
                            onClear()
                            keyboard?.hide()
                        },
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
    )
}

private object SearchFieldDefaults {
    val IconSize: Dp = 20.dp
}
