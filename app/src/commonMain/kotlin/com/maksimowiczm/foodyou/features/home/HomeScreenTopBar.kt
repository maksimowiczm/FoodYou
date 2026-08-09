package com.maksimowiczm.foodyou.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfade
import com.maksimowiczm.foodyou.shared.ui.brand
import com.maksimowiczm.foodyou.shared.ui.component.Avatar
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.abs
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeScreenTopBar(
    profile: ProfileUiState?,
    profiles: List<ProfileUiState>,
    textFieldState: TextFieldState,
    homeProgress: () -> Float,
    onAvatar: () -> Unit,
    onSearch: (String?) -> Unit,
    onSearchBar: () -> Unit,
    onBack: () -> Unit,
    onSelectProfile: (ProfileUiState) -> Unit,
    onBarcodeScanner: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme

    TopBarLayout(
        modifier = modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets),
        progress = homeProgress,
        startSlot = {
            IconButton(
                onClick = onMenu,
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.graphicsLayer { alpha = homeProgress() },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize(unbounded = true),
                )
            }
        },
        endSlot = {
            var profileSwitchDirection by remember { mutableIntStateOf(1) }
            var animateProfileSwitch by remember { mutableStateOf(false) }

            // Reset after changing the profile
            LaunchedEffect(profile) { animateProfileSwitch = false }

            AnimatedContent(
                targetState = profile,
                modifier = Modifier.graphicsLayer { alpha = homeProgress() },
                transitionSpec = {
                    if (!animateProfileSwitch) {
                        EnterTransition.None.togetherWith(ExitTransition.None)
                    } else if (profileSwitchDirection < 0) {
                        slideInVertically(motionScheme.fastSpatialSpec()) { -it } +
                            fadeIn(motionScheme.fastEffectsSpec()) togetherWith
                            slideOutVertically(motionScheme.fastSpatialSpec()) { it } +
                                fadeOut(motionScheme.fastEffectsSpec())
                    } else {
                        slideInVertically(motionScheme.fastSpatialSpec()) { it } +
                            fadeIn(motionScheme.fastEffectsSpec()) togetherWith
                            slideOutVertically(motionScheme.fastSpatialSpec()) { -it } +
                                fadeOut(motionScheme.fastEffectsSpec())
                    }
                },
            ) { p ->
                IconButton(
                    onClick = onAvatar,
                    shapes = IconButtonDefaults.shapes(),
                    modifier =
                        Modifier.wrapContentSize(unbounded = true).swipeThroughList(
                            items = profiles,
                            currentItem = p,
                            swipeThresholdPx = LocalDensity.current.run { 32.dp.toPx() },
                        ) { direction, nextProfile ->
                            profileSwitchDirection = direction
                            animateProfileSwitch = true
                            onSelectProfile(nextProfile)
                        },
                ) {
                    if (p != null) {
                        val avatarModifier =
                            when (p.avatar) {
                                is Profile.Avatar.Photo -> Modifier.size(40.dp)
                                is Profile.Avatar.Predefined ->
                                    Modifier.padding(8.dp)
                                        .size(24.dp)
                                        .wrapContentSize(unbounded = true)
                            }
                        p.avatar.Avatar(avatarModifier)
                    } else {
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )
                    }
                }
            }
        },
    ) {
        SearchBarContent(
            textFieldState = textFieldState,
            homeProgress = homeProgress,
            onClick = onSearchBar,
            onBack = onBack,
            onSearch = onSearch,
            onClear = { onSearch(null) },
            onBarcodeScanner = onBarcodeScanner,
        )
    }
}

@Composable
private fun TopBarLayout(
    progress: () -> Float,
    startSlot: @Composable () -> Unit,
    endSlot: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // We have to use it in composable scope, because if we do it in layout scope then weird things
    // happen with icon button shape, might be a bug in material library. It still happens, but it's
    // not as extreme as it would be without this guard
    val showSlots by remember(progress) { derivedStateOf { progress() > 0f } }

    Layout(
        modifier = modifier.height(64.dp),
        content = {
            if (showSlots) Box(contentAlignment = Alignment.Center) { startSlot() }
            Box(contentAlignment = Alignment.Center) { content() }
            if (showSlots) Box(contentAlignment = Alignment.Center) { endSlot() }
        },
    ) { measurables, constraints ->
        val p = progress()

        val totalWidth = constraints.maxWidth
        val height = constraints.maxHeight

        val slotPx = lerp(0.dp, 48.dp, p).roundToPx().coerceAtLeast(0)
        val hPadPx = lerp(12.dp, 4.dp, p).roundToPx().coerceAtLeast(0)
        val gapPx = lerp(0.dp, 4.dp, p).roundToPx().coerceAtLeast(0)

        val startM = if (measurables.size == 3) measurables[0] else null
        val centerM = if (measurables.size == 3) measurables[1] else measurables[0]
        val endM = if (measurables.size == 3) measurables[2] else null

        val startP = startM?.measure(Constraints.fixed(slotPx, height))
        val endP = endM?.measure(Constraints.fixed(slotPx, height))

        val centerWidth = (totalWidth - slotPx * 2 - hPadPx * 2 - gapPx * 2).coerceAtLeast(0)
        val centerP = centerM.measure(Constraints.fixed(centerWidth, height))

        layout(totalWidth, height) {
            startP?.placeRelative(hPadPx, 0)
            centerP.placeRelative(hPadPx + slotPx + gapPx, 0)
            endP?.placeRelative(totalWidth - hPadPx - slotPx, 0)
        }
    }
}

@Composable
private fun SearchBarContent(
    textFieldState: TextFieldState,
    homeProgress: () -> Float,
    onClick: () -> Unit,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeProgress = homeProgress()
    val isHome = homeProgress == 1f

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(
            Modifier.sizeIn(
                    minWidth = 360.dp,
                    maxWidth = 720.dp,
                    minHeight = SearchBarDefaults.InputFieldHeight,
                    maxHeight = SearchBarDefaults.InputFieldHeight,
                )
                .padding(horizontal = 4.dp)
        ) {
            if (!isHome) {
                SearchInputField(
                    textFieldState = textFieldState,
                    onSearch = onSearch,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (isHome) {
                Box(
                    modifier = Modifier.size(48.dp).align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(Res.string.action_search),
                    )
                }
            } else {
                IconButton(
                    onClick = onBack,
                    shapes = IconButtonDefaults.shapes(),
                    modifier =
                        Modifier.align(Alignment.CenterStart).graphicsLayer {
                            alpha = 1f - homeProgress()
                        },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronLeft,
                        contentDescription = stringResource(Res.string.action_close),
                    )
                }
            }
            if (textFieldState.text.isEmpty() || isHome) {
                updateTransition(isHome, "placeholder").AnimatedContent(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                    transitionSpec = { crossfade() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (it)
                        Text(
                            text = stringResource(Res.string.app_name),
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.brand.bodyLarge,
                            maxLines = 1,
                        )
                    else
                        Text(
                            text = stringResource(Res.string.action_search_foods),
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                if (!isHome && textFieldState.text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            textFieldState.clearText()
                            onClear()
                        },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(Res.string.action_clear),
                        )
                    }
                }
                IconButton(onClick = onBarcodeScanner, shapes = IconButtonDefaults.shapes()) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_barcode_scanner),
                        contentDescription = stringResource(Res.string.action_scan_barcode),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchInputField(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colors = SearchBarDefaults.inputFieldColors()

    BasicTextField(
        state = textFieldState,
        modifier = modifier.sizeIn(minHeight = SearchBarDefaults.InputFieldHeight),
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = MaterialTheme.typography.bodyLarge.merge(MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(colors.cursorColor(isError = false)),
        keyboardOptions =
            KeyboardOptions.Default.merge(KeyboardOptions(imeAction = ImeAction.Search)),
        onKeyboardAction = { onSearch(textFieldState.text.toString()) },
        interactionSource = interactionSource,
        decorator =
            TextFieldDefaults.decorator(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                enabled = true,
                outputTransformation = null,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = PaddingValues(start = 44.dp, end = 92.dp),
                container = { Box(Modifier) },
            ),
    )
}

/**
 * Detects a vertical swipe and switches to the previous or next item in [items] once the
 * accumulated drag reaches [swipeThresholdPx].
 *
 * Positive drag selects the previous item, negative drag selects the next item. The list is treated
 * as closed loop, so swiping past either end wraps around.
 */
private fun <T> Modifier.swipeThroughList(
    items: List<T>,
    currentItem: T?,
    swipeThresholdPx: Float,
    onItemSwitched: (direction: Int, nextItem: T) -> Unit,
): Modifier =
    pointerInput(items, currentItem, swipeThresholdPx) {
        var totalDrag = 0f
        var profileSwitched = false

        detectVerticalDragGestures(
            onDragStart = {
                totalDrag = 0f
                profileSwitched = false
            },
            onVerticalDrag = { change, dragAmount ->
                totalDrag += dragAmount
                if (!profileSwitched && abs(totalDrag) >= swipeThresholdPx) {
                    val nextItem =
                        adjacentItem(
                            items = items,
                            current = currentItem,
                            movePrevious = totalDrag > 0f,
                        )
                    if (nextItem != null && nextItem != currentItem) {
                        onItemSwitched(if (totalDrag > 0f) -1 else 1, nextItem)
                        profileSwitched = true
                    }
                }
                change.consume()
            },
        )
    }

private fun <T> adjacentItem(items: List<T>, current: T?, movePrevious: Boolean): T? {
    if (items.isEmpty()) {
        return null
    }

    if (items.size == 1) {
        return items.first()
    }

    val currentIndex = current?.let(items::indexOf) ?: -1
    if (currentIndex == -1) {
        return if (movePrevious) items.last() else items.first()
    }

    val offset = if (movePrevious) -1 else 1
    val nextIndex = (currentIndex + offset + items.size) % items.size
    return items[nextIndex]
}
