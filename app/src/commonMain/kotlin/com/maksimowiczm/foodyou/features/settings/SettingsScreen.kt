package com.maksimowiczm.foodyou.features.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.Avatar
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.toDp
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (ProfileId) -> Unit,
    onPersonalization: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingsViewModel: SettingsViewModel = koinInject()
    val profiles by settingsViewModel.profiles.collectAsStateWithLifecycle()
    val selectedProfileId by settingsViewModel.selectedProfile.collectAsStateWithLifecycle()
    val profile =
        remember(profiles, selectedProfileId) { profiles?.find { it.id == selectedProfileId } }

    SettingsScreen(
        profiles = profiles,
        selectedProfile = profile,
        onSelectProfile = settingsViewModel::selectProfile,
        onAddProfile = onAddProfile,
        onEditProfile = onEditProfile,
        onBack = onBack,
        onPersonalization = onPersonalization,
        onLanguage = onLanguage,
        onPrivacy = onPrivacy,
        onAbout = onAbout,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    profiles: List<Profile>?,
    selectedProfile: Profile?,
    onSelectProfile: (ProfileId) -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (ProfileId) -> Unit,
    onBack: () -> Unit,
    onPersonalization: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var profileExpanded by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = contentPadding.add(8.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AnimatedVisibility(
                        visible = profileExpanded,
                        enter =
                            fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                                expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
                        exit =
                            fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                                shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AnimatedAvatar(
                                settingsProfileUiState = selectedProfile,
                                onEditProfile = { onEditProfile(it) },
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    AnimatedName(selectedProfile?.name)
                }
            }
            item {
                ProfileSwitcher(
                    profiles = profiles,
                    selectedProfile = selectedProfile,
                    expanded = !profileExpanded,
                    onExpandedChange = { profileExpanded = !it },
                    onSelectProfile = onSelectProfile,
                    onAddProfile = onAddProfile,
                    onEditProfile = onEditProfile,
                )
            }
            item {
                Settings(
                    onPersonalization = onPersonalization,
                    onLanguage = onLanguage,
                    onPrivacy = onPrivacy,
                    onAbout = onAbout,
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedAvatar(
    settingsProfileUiState: Profile?,
    onEditProfile: (ProfileId) -> Unit,
    modifier: Modifier = Modifier,
) {
    updateTransition(targetState = settingsProfileUiState).Crossfade(modifier) { profile ->
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val cornerDp = animateDpAsState(if (isPressed.value) 8.dp else 32.dp)

        Box(
            Modifier.size(64.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = profile != null,
                    onClick = { if (profile != null) onEditProfile(profile.id) },
                    onClickLabel = stringResource(Res.string.action_edit),
                    role = Role.Button,
                )
        ) {
            Box(
                Modifier.matchParentSize().align(Alignment.Center).graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(cornerDp.value)
                }
            ) {
                when (val avatar = profile?.avatar) {
                    null ->
                        Spacer(
                            Modifier.shimmer()
                                .matchParentSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )

                    is Profile.Avatar.Photo -> avatar.Avatar(Modifier.matchParentSize())

                    is Profile.Avatar.Predefined ->
                        avatar.Avatar(Modifier.padding(8.dp).matchParentSize())
                }
            }
            if (profile != null) {
                IconButton(
                    onClick = { onEditProfile(profile.id) },
                    modifier =
                        Modifier.size(24.dp).align(Alignment.BottomEnd).clearAndSetSemantics {},
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    shapes = IconButtonDefaults.shapes(),
                    interactionSource = interactionSource,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedName(
    name: String?,
    modifier: Modifier = Modifier,
) {
    if (name != null) {
        val colorScheme = MaterialTheme.colorScheme
        val colors =
            remember(colorScheme) {
                listOf(colorScheme.primary, colorScheme.secondary, colorScheme.tertiary)
            }

        val infiniteTransition = rememberInfiniteTransition()
        val offset =
            infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 10_000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
            )
        val brush =
            remember(offset) {
                object : ShaderBrush() {
                    override fun createShader(size: Size): Shader {
                        val widthOffset = size.width * offset.value
                        val heightOffset = size.height * offset.value
                        return LinearGradientShader(
                            colors = colors,
                            from = Offset(widthOffset, heightOffset),
                            to = Offset(widthOffset + size.width, heightOffset + size.height),
                            tileMode = TileMode.Mirror,
                        )
                    }
                }
            }

        Text(
            text =
                stringResource(
                    Res.string.headline_welcome_user_message,
                    name,
                ),
            modifier = modifier,
            style = MaterialTheme.typography.titleLarge.copy(brush = brush),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Box(
            modifier
                .shimmer()
                .height(MaterialTheme.typography.titleLarge.toDp())
                .width(150.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ProfileSwitcher(
    profiles: List<Profile>?,
    selectedProfile: Profile?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectProfile: (ProfileId) -> Unit,
    onEditProfile: (ProfileId) -> Unit,
    onAddProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandedTransition = updateTransition(expanded)
    val iconRotationState =
        animateFloatAsState(
            if (expanded) 0f else 180f,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    val colors =
        ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )

    val listItemCount = remember(profiles) { (profiles?.size ?: 0) + 2 }

    Column(modifier) {
        val headerInteractionSource = remember { MutableInteractionSource() }
        SegmentedListItem(
            onClick = { onExpandedChange(!expanded) },
            shapes =
                ListItemDefaults.segmentedShapes(
                    index = 0,
                    count = if (expanded) listItemCount else 1,
                    defaultShapes =
                        if (expanded) ListItemDefaults.shapes()
                        else ListItemDefaults.shapes(shape = MaterialTheme.shapes.medium),
                ),
            colors = colors,
            trailingContent = {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = iconRotationState.value },
                )
            },
            content = {
                expandedTransition.Crossfade { expanded ->
                    if (expanded || selectedProfile == null) {
                        Box(
                            modifier = Modifier.height(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(stringResource(Res.string.action_switch_profile))
                        }
                    } else {
                        Row(
                            modifier = Modifier.height(40.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(stringResource(Res.string.action_switch_profile))
                            Spacer(Modifier.weight(1f))
                            Row(
                                modifier = Modifier.height(40.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                profiles
                                    ?.filter { it != selectedProfile }
                                    ?.take(3)
                                    ?.forEach { avatar ->
                                        when (val avatar = avatar.avatar) {
                                            is Profile.Avatar.Photo ->
                                                avatar.Avatar(
                                                    Modifier.size(24.dp).clip(CircleShape)
                                                )

                                            is Profile.Avatar.Predefined ->
                                                avatar.Avatar(Modifier.size(24.dp))
                                        }
                                    }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.heightIn(min = 60.dp),
            interactionSource = headerInteractionSource,
        )
        AnimatedVisibility(
            visible = expanded,
            enter =
                fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()),
            exit =
                fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    shrinkVertically(MaterialTheme.motionScheme.defaultSpatialSpec()),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Spacer(Modifier)
                profiles?.forEachIndexed { index, profile ->
                    key(profile.id.value) {
                        val interactionSource = remember { MutableInteractionSource() }
                        SegmentedListItem(
                            onClick = {
                                if (profile == selectedProfile) onEditProfile(profile.id)
                                else onSelectProfile(profile.id)
                            },
                            selected = profile == selectedProfile,
                            colors = colors,
                            shapes =
                                ListItemDefaults.segmentedShapes(
                                    index = index + 1,
                                    count = listItemCount,
                                ),
                            leadingContent = {
                                val modifier =
                                    when (profile.avatar) {
                                        is Profile.Avatar.Photo -> Modifier.size(40.dp)

                                        is Profile.Avatar.Predefined ->
                                            Modifier.padding(8.dp).size(24.dp)
                                    }

                                when (val avatar = profile.avatar) {
                                    is Profile.Avatar.Photo ->
                                        AnimatedAvatar(avatar, interactionSource, modifier)

                                    is Profile.Avatar.Predefined -> avatar.Avatar(modifier)
                                }
                            },
                            trailingContent =
                                if (profile == selectedProfile) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription =
                                                stringResource(Res.string.action_show_more),
                                        )
                                    }
                                } else null,
                            content = {
                                Text(
                                    text = profile.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            modifier = Modifier.heightIn(min = 60.dp),
                            interactionSource = interactionSource,
                        )
                    }
                }
                SegmentedListItem(
                    onClick = onAddProfile,
                    colors = colors,
                    shapes =
                        ListItemDefaults.segmentedShapes(
                            index = listItemCount - 1,
                            count = listItemCount,
                        ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp).size(24.dp),
                        )
                    },
                    content = { Text(stringResource(Res.string.action_add_profile)) },
                    modifier = Modifier.heightIn(min = 60.dp),
                )
            }
        }
    }
}

@Composable
private fun AnimatedAvatar(
    avatar: Profile.Avatar,
    interactionSource: InteractionSource,
    modifier: Modifier = Modifier,
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedCorner = animateDpAsState(if (isPressed) 4.dp else 1000.dp)

    avatar.Avatar(
        modifier.graphicsLayer {
            clip = true
            shape = RoundedCornerShape(animatedCorner.value)
        }
    )
}

@Composable
private fun Settings(
    onPersonalization: () -> Unit,
    onLanguage: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SegmentedListItem(
            onClick = onPersonalization,
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 4),
            leadingContent = { Icon(Icons.Outlined.Tune, contentDescription = null) },
            colors = colors,
            content = { Text(stringResource(Res.string.headline_personalization)) },
        )
        SegmentedListItem(
            onClick = onLanguage,
            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 4),
            leadingContent = { Icon(Icons.Outlined.Translate, contentDescription = null) },
            colors = colors,
            content = { Text(stringResource(Res.string.headline_language)) },
        )
        SegmentedListItem(
            onClick = onPrivacy,
            shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
            leadingContent = { Icon(Icons.Outlined.PrivacyTip, contentDescription = null) },
            colors = colors,
            content = { Text(stringResource(Res.string.headline_privacy)) },
        )
        SegmentedListItem(
            onClick = onAbout,
            shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
            leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
            colors = colors,
            content = { Text(stringResource(Res.string.headline_about)) },
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    val profiles =
        listOf(
            Profile(
                name = "Mateusz",
                avatar = Profile.Avatar.Predefined.Variant.Engineer.toAvatar(),
            ),
            Profile(
                name = "Maksimowicz",
                avatar = Profile.Avatar.Predefined.Variant.Person.toAvatar(),
            ),
            Profile(
                name = "MaMa",
                avatar = Profile.Avatar.Predefined.Variant.Woman.toAvatar(),
            ),
        )
    PreviewFoodYouTheme {
        SettingsScreen(
            profiles = profiles,
            selectedProfile = profiles[1],
            onSelectProfile = {},
            onAddProfile = {},
            onEditProfile = {},
            onBack = {},
            onPersonalization = {},
            onLanguage = {},
            onPrivacy = {},
            onAbout = {},
        )
    }
}
