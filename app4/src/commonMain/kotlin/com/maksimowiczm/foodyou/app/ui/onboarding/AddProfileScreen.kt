package com.maksimowiczm.foodyou.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person3
import androidx.compose.material.icons.outlined.Person4
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.onboarding_privacy_tip
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    onSetAvatar: (UiAvatar) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_add_profile)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            item {
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
                        items(items = UiAvatar.entries) { avatar ->
                            AvatarItem(
                                avatar = avatar,
                                isSelected = uiState.avatar == avatar,
                                onSelect = { onSetAvatar(avatar) },
                            )
                        }
                    }
                }
            }

            item {
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
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item {
                Text(
                    text = stringResource(Res.string.onboarding_privacy_tip),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun AvatarItem(
    avatar: UiAvatar,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vector =
        when (avatar) {
            UiAvatar.PERSON -> Icons.Outlined.Person
            UiAvatar.WOMAN -> Icons.Outlined.Person3
            UiAvatar.MAN -> Icons.Outlined.Person4
            UiAvatar.ENGINEER -> Icons.Outlined.Engineering
        }

    FilledIconToggleButton(
        checked = isSelected,
        onCheckedChange = { onSelect() },
        shapes = IconButtonDefaults.toggleableShapes(),
        modifier = modifier.size(56.dp),
    ) {
        Icon(
            imageVector = vector,
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.mediumIconSize),
        )
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
