package com.maksimowiczm.foodyou.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar.Predefined.Type
import foodyou.app.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileForm(
    uiState: ProfileUiState,
    onSetAvatar: (UiProfileAvatar) -> Unit,
    autoFocusName: Boolean,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

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
                items(items = Type.entries.map { it.toAvatar() }) { avatar ->
                    FilledIconToggleButton(
                        checked = uiState.avatar == avatar,
                        onCheckedChange = { if (it) onSetAvatar(avatar) },
                        shapes = IconButtonDefaults.toggleableShapes(),
                        modifier = modifier.size(56.dp),
                    ) {
                        avatar.Avatar(Modifier.size(24.dp).clip(CircleShape))
                    }
                }
                item {
                    FilledIconToggleButton(
                        checked = uiState.avatar is UiProfileAvatar.Photo,
                        onCheckedChange = {
                            scope.launch {
                                val image = FileKit.openFilePicker(type = FileKitType.Image)
                                if (image != null) {
                                    onSetAvatar(UiProfileAvatar.Photo(image.path))
                                }
                            }
                        },
                        shapes = IconButtonDefaults.toggleableShapes(),
                        modifier = modifier.size(56.dp),
                    ) {
                        when (uiState.avatar) {
                            is UiProfileAvatar.Photo ->
                                uiState.avatar.Avatar(Modifier.size(40.dp).clip(CircleShape))

                            else ->
                                Icon(
                                    imageVector = Icons.Outlined.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
                                )
                        }
                    }
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
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                enabled = !uiState.isLocked,
                placeholder = { Text(stringResource(Res.string.headline_profile_name)) },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
        }
    }
}
