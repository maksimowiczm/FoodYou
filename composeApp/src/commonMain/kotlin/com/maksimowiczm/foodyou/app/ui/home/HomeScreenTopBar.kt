package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.maksimowiczm.foodyou.app.navigation.Crossfade.crossfade
import com.maksimowiczm.foodyou.app.ui.common.component.UiProfileAvatar
import com.maksimowiczm.foodyou.app.ui.common.theme.brand
import com.maksimowiczm.foodyou.app.ui.home.search.HomeSearchState
import com.maksimowiczm.foodyou.app.ui.home.search.Search
import com.maksimowiczm.foodyou.app.ui.home.search.SearchView
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.abs
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeScreenTopBar(
    profile: ProfileUiState?,
    profiles: List<ProfileUiState>,
    homeSearchState: HomeSearchState,
    onAvatar: () -> Unit,
    onSearch: (String?) -> Unit,
    onSelectProfile: (ProfileUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val motionScheme = MaterialTheme.motionScheme

    val homeAnimatable = remember { Animatable(if (homeSearchState.isHome) 1f else 0f) }
    LaunchedEffect(homeSearchState.isHome) {
        if (homeSearchState.backProgressAnimatable.value != 0f) {
            homeAnimatable.snapTo(homeSearchState.backProgressAnimatable.value)
        }
        homeAnimatable.animateTo(
            targetValue = if (homeSearchState.isHome) 1f else 0f,
            animationSpec = motionScheme.fastSpatialSpec(),
        )
    }

    val homeProgress = remember {
        derivedStateOf {
            val gesture = homeSearchState.backProgressAnimatable.value
            if (gesture != 0f) gesture else homeAnimatable.value
        }
    }

    TopBarLayout(
        modifier = modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets),
        progress = { homeProgress.value },
        startSlot = {
            IconButton(
                onClick = { scope.launch { homeSearchState.railState.expand() } },
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.graphicsLayer { alpha = homeProgress.value },
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
                modifier = Modifier.graphicsLayer { alpha = homeProgress.value },
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
                        Modifier.swipeThroughList(
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
                                is UiProfileAvatar.Photo -> Modifier.size(40.dp)
                                is UiProfileAvatar.Predefined ->
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
            homeSearchState = homeSearchState,
            homeProgress = { homeProgress.value },
            onSearchClick = { scope.launch { homeSearchState.goToSearchView() } },
            onBackClick = { scope.launch { homeSearchState.popToHome() } },
            onSearchAction = { onSearch(homeSearchState.textFieldState.text.toString()) },
            onClearClick = {
                homeSearchState.textFieldState.clearText()
                if (homeSearchState.backStack.last() is Search) {
                    onSearch(null)
                }
            },
            onBarcodeScannerClick = { homeSearchState.showBarcodeScanner = true },
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
    Layout(
        modifier = modifier.height(64.dp),
        content = {
            Box(contentAlignment = Alignment.Center) { startSlot() }
            Box(contentAlignment = Alignment.Center) { content() }
            Box(contentAlignment = Alignment.Center) { endSlot() }
        },
    ) { measurables, constraints ->
        val p = progress()

        val totalWidth = constraints.maxWidth
        val height = constraints.maxHeight

        val slotPx = lerp(0.dp, 48.dp, p).roundToPx().coerceAtLeast(0)
        val hPadPx = lerp(12.dp, 4.dp, p).roundToPx().coerceAtLeast(0)
        val gapPx = lerp(0.dp, 4.dp, p).roundToPx().coerceAtLeast(0)

        val (startM, centerM, endM) = measurables

        val startP = startM.measure(Constraints.fixed(slotPx, height))
        val endP = endM.measure(Constraints.fixed(slotPx, height))

        val centerWidth = (totalWidth - slotPx * 2 - hPadPx * 2 - gapPx * 2).coerceAtLeast(0)
        val centerP = centerM.measure(Constraints.fixed(centerWidth, height))

        layout(totalWidth, height) {
            startP.placeRelative(hPadPx, 0)
            centerP.placeRelative(hPadPx + slotPx + gapPx, 0)
            endP.placeRelative(totalWidth - hPadPx - slotPx, 0)
        }
    }
}

@Composable
private fun SearchBarContent(
    homeSearchState: HomeSearchState,
    homeProgress: () -> Float,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchAction: () -> Unit,
    onClearClick: () -> Unit,
    onBarcodeScannerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onSearchClick,
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
            if (homeSearchState.showSearchField) {
                SearchInputField(
                    homeSearchState = homeSearchState,
                    onSearchAction = onSearchAction,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (homeSearchState.isHome) {
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
                    onClick = onBackClick,
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
            if (homeSearchState.textFieldState.text.isEmpty()) {
                updateTransition(homeSearchState.isHome, "placeholder").AnimatedContent(
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
                if (!homeSearchState.isHome && homeSearchState.textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = onClearClick, shapes = IconButtonDefaults.shapes()) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(Res.string.action_clear),
                        )
                    }
                }
                IconButton(onClick = onBarcodeScannerClick, shapes = IconButtonDefaults.shapes()) {
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
    homeSearchState: HomeSearchState,
    onSearchAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(homeSearchState.backStack) {
        if (homeSearchState.backStack.last() is SearchView) {
            focusRequester.requestFocus()
        }
    }

    val colors = SearchBarDefaults.inputFieldColors()

    BasicTextField(
        state = homeSearchState.textFieldState,
        modifier =
            modifier
                .focusRequester(focusRequester)
                .sizeIn(minHeight = SearchBarDefaults.InputFieldHeight),
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = MaterialTheme.typography.bodyLarge.merge(MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(colors.cursorColor(isError = false)),
        keyboardOptions =
            KeyboardOptions.Default.merge(KeyboardOptions(imeAction = ImeAction.Search)),
        onKeyboardAction = { onSearchAction() },
        interactionSource = interactionSource,
        decorator =
            TextFieldDefaults.decorator(
                state = homeSearchState.textFieldState,
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
