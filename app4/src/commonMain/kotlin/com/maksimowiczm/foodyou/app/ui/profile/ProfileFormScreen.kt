package com.maksimowiczm.foodyou.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import foodyou.app.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileFormScreen(
    uiState: ProfileUiState,
    onSetAvatar: (UiProfileAvatar) -> Unit,
    autoFocusName: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (autoFocusName) {
            delay(300)
            focusRequester.requestFocus()
        }
    }

    Column(modifier) {
        Text(
            text = stringResource(Res.string.onboarding_privacy_tip),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.headline_profile_picture),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelLarge,
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(items = UiProfileAvatar.entries) { avatar ->
                    AvatarItem(
                        avatar = avatar,
                        isSelected = uiState.avatar == avatar,
                        onSelect = { onSetAvatar(avatar) },
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.headline_how_do_you_want_to_be_called),
                style = MaterialTheme.typography.labelLarge,
            )
            OutlinedTextField(
                state = uiState.nameTextState,
                placeholder = { Text(stringResource(Res.string.headline_profile_name)) },
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            )
        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
    }
}

@Composable
private fun AvatarItem(
    avatar: UiProfileAvatar,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconToggleButton(
        checked = isSelected,
        onCheckedChange = { onSelect() },
        shapes = IconButtonDefaults.toggleableShapes(),
        modifier = modifier.size(56.dp),
    ) {
        Icon(
            imageVector = avatar.toImageVector(),
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
        )
    }
}
