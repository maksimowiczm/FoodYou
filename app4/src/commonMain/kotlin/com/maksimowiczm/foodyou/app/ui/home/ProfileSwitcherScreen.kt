package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.app.ui.common.saveable.rememberBlockingDataStore
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileSwitcherScreen(
    onFoodDatabase: () -> Unit,
    onPersonalization: () -> Unit,
    onDataBackupAndExport: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    onAddProfile: () -> Unit,
    onSelectProfile: (ProfileUiState) -> Unit,
    onEditProfile: (ProfileUiState) -> Unit,
    profiles: List<ProfileUiState>,
    selectedProfile: ProfileUiState,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets =
        ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Vertical + WindowInsetsSides.End
        ),
) {
    Scaffold(modifier = modifier, contentWindowInsets = windowInsets) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ProfileSwitcher(
                    profiles = profiles,
                    selectedProfile = selectedProfile,
                    onSelectProfile = onSelectProfile,
                    onAddProfile = onAddProfile,
                    onEditProfile = onEditProfile,
                )
            }

            item {
                SettingsGroup {
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.LocalDining,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = { Text(stringResource(Res.string.headline_food_database)) },
                        onClick = onFoodDatabase,
                    )
                }
            }
            item {
                SettingsGroup {
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = { Text(stringResource(Res.string.headline_personalization)) },
                        onClick = onPersonalization,
                    )
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_database),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = {
                            Text(stringResource(Res.string.headline_data_backup_and_export))
                        },
                        onClick = onDataBackupAndExport,
                    )
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Translate,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = { Text(stringResource(Res.string.headline_language)) },
                        onClick = onLanguage,
                    )
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.PrivacyTip,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = { Text(stringResource(Res.string.headline_privacy)) },
                        onClick = onPrivacy,
                    )
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        title = { Text(stringResource(Res.string.headline_about)) },
                        onClick = onAbout,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSwitcher(
    profiles: List<ProfileUiState>,
    selectedProfile: ProfileUiState,
    onSelectProfile: (ProfileUiState) -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (ProfileUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by
        rememberBlockingDataStore(key = booleanPreferencesKey(":ui:home:expandProfileSwitcher")) {
            mutableStateOf(true)
        }
    val iconRotationState by animateFloatAsState(if (expanded) 0f else 180f)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SettingsListItem(
            title = { Text(stringResource(Res.string.action_switch_profile)) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(visible = !expanded, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = selectedProfile.name,
                            modifier = Modifier.padding(end = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { rotationZ = iconRotationState },
                    )
                }
            },
            onClick = { expanded = !expanded },
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = animateDpAsState(if (expanded) 8.dp else 16.dp).value,
                    bottomEnd = animateDpAsState(if (expanded) 8.dp else 16.dp).value,
                ),
        )
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                profiles.forEach { profile ->
                    SettingsListItem(
                        leadingIcon = {
                            Icon(
                                imageVector = profile.avatar.toImageVector(),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                            )
                        },
                        trailingIcon = {
                            if (profile == selectedProfile) {
                                IconButton(
                                    onClick = { onEditProfile(profile) },
                                    shapes = IconButtonDefaults.shapes(),
                                    modifier =
                                        Modifier.size(
                                            IconButtonDefaults.extraSmallContainerSize(
                                                IconButtonDefaults.IconButtonWidthOption.Uniform
                                            )
                                        ),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.MoreVert,
                                        contentDescription = stringResource(Res.string.action_edit),
                                        modifier =
                                            Modifier.size(IconButtonDefaults.extraSmallIconSize),
                                    )
                                }
                            }
                        },
                        title = {
                            Text(
                                text = profile.name,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        onClick = { onSelectProfile(profile) },
                        containerColor =
                            if (profile == selectedProfile)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer,
                        contentColor =
                            if (profile == selectedProfile)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                    )
                }
                SettingsListItem(
                    title = { Text(stringResource(Res.string.action_add_profile)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
                        )
                    },
                    onClick = onAddProfile,
                    shape =
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp,
                        ),
                )
            }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.clip(MaterialTheme.shapes.large),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        content = content,
    )
}

@Composable
private fun SettingsListItem(
    title: @Composable () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shapes =
            ButtonDefaults.shapes(shape = shape, pressedShape = MaterialTheme.shapes.extraLarge),
        modifier = modifier,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            leadingIcon?.invoke()
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelLarge) {
                title()
            }
            Spacer(Modifier.weight(1f))
            trailingIcon?.invoke()
        }
    }
}
