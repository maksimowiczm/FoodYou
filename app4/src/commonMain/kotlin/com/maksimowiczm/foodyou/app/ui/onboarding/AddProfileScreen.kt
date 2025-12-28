package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar.Predefined.Type
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddProfileScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    AddProfileScreen(
        uiState = uiState,
        onSetName = viewModel::setProfileName,
        onSetAvatar = viewModel::setAvatar,
        onBack = onBack,
        onContinue = onContinue,
        modifier = modifier,
    )
}

@Composable
private fun AddProfileScreen(
    uiState: OnboardingUiState,
    onSetName: (String) -> Unit,
    onSetAvatar: (UiProfileAvatar) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_add_profile)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .verticalScroll(rememberScrollState())
        ) {
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
                    value = uiState.profileName,
                    onValueChange = onSetName,
                    placeholder = { Text(stringResource(Res.string.headline_profile_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardActions =
                        KeyboardActions {
                            if (uiState.isProfileValid) {
                                onContinue()
                            }
                        },
                )
            }
            Spacer(Modifier.height(32.dp))
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
            Text(
                text = stringResource(Res.string.description_not_sure_yet),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val fabHeight = 56.dp
                Button(
                    onClick = onContinue,
                    shapes = ButtonDefaults.shapesFor(fabHeight),
                    modifier = Modifier.widthIn(min = 250.dp),
                    enabled = uiState.isProfileValid,
                    contentPadding = ButtonDefaults.contentPaddingFor(fabHeight),
                ) {
                    Text(
                        text = stringResource(Res.string.action_continue),
                        style = ButtonDefaults.textStyleFor(fabHeight),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun AddProfileScreenPreview() {
    PreviewFoodYouTheme {
        AddProfileScreen(
            uiState = OnboardingUiState(),
            onSetName = {},
            onSetAvatar = {},
            onBack = {},
            onContinue = {},
        )
    }
}
