package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.app.navigation.Crossfade
import com.maksimowiczm.foodyou.common.domain.food.sum
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.shared.ui.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.brand
import com.maksimowiczm.foodyou.shared.ui.component.Avatar
import com.maksimowiczm.foodyou.shared.ui.extension.toDp
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import foodyou.app.generated.resources.*
import kotlin.math.abs
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenTopBar(
    profile: ProfileUiState?,
    profiles: List<ProfileUiState>,
    showMeal: Boolean,
    meal: HomeMealState?,
    date: LocalDate,
    textFieldState: TextFieldState,
    shimmer: Shimmer,
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
                            Modifier.shimmer(shimmer)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )
                    }
                }
            }
        },
    ) {
        Column {
            AnimatedVisibility(
                visible = showMeal && meal != null,
                modifier = Modifier.fillMaxWidth(),
                enter =
                    expandVertically(motionScheme.fastSpatialSpec()) +
                        fadeIn(motionScheme.fastEffectsSpec()),
                exit =
                    shrinkVertically(motionScheme.defaultSpatialSpec()) +
                        fadeOut(motionScheme.defaultEffectsSpec()),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (meal != null)
                        Text(
                            text = meal.name,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.brand.displayMedium,
                            textAlign = TextAlign.Center,
                        )
                    else
                        Spacer(
                            Modifier.shimmer(shimmer)
                                .height(MaterialTheme.typography.brand.displayMedium.toDp() - 8.dp)
                                .width(200.dp)
                                .padding(vertical = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        )
                    Text(
                        text = LocalDateFormatter.current.formatDateShort(date),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(8.dp))
                    val nutrientsOrder = LocalNutrientsOrder.current
                    val nutrientsPalette = LocalNutrientsPalette.current
                    if (meal != null) {
                        val nutrition =
                            remember(meal.foods) {
                                meal.foods.map { it.component.measuredNutritionFacts }.sum()
                            }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val energy = nutrition.energy.value ?: 0.kilocalories
                            item {
                                Box(
                                    modifier =
                                        Modifier.heightIn(min = 40.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text =
                                            energy.inUnit(LocalEnergyUnit.current).stringResource(),
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    )
                                }
                            }
                            nutrientsOrder.forEach {
                                when (it) {
                                    NutrientsOrder.Proteins -> {
                                        val proteins = nutrition.proteins.value ?: 0.grams
                                        item {
                                            Surface(color = MaterialTheme.colorScheme.surface) {
                                                Box(
                                                    modifier =
                                                        Modifier.heightIn(min = 40.dp)
                                                            .clip(MaterialTheme.shapes.medium)
                                                            .background(
                                                                nutrientsPalette
                                                                    .proteinsOnSurfaceContainer
                                                                    .copy(alpha = .25f)
                                                            ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = proteins.stringResource(),
                                                        modifier =
                                                            Modifier.padding(horizontal = 16.dp),
                                                        color =
                                                            nutrientsPalette
                                                                .proteinsOnSurfaceContainer,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    NutrientsOrder.Fats -> {
                                        val proteins = nutrition.fats.value ?: 0.grams
                                        item {
                                            Surface(color = MaterialTheme.colorScheme.surface) {
                                                Box(
                                                    modifier =
                                                        Modifier.heightIn(min = 40.dp)
                                                            .clip(MaterialTheme.shapes.medium)
                                                            .background(
                                                                nutrientsPalette
                                                                    .fatsOnSurfaceContainer
                                                                    .copy(alpha = .25f)
                                                            ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = proteins.stringResource(),
                                                        modifier =
                                                            Modifier.padding(horizontal = 16.dp),
                                                        color =
                                                            nutrientsPalette.fatsOnSurfaceContainer,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    NutrientsOrder.Carbohydrates -> {
                                        val proteins = nutrition.carbohydrates.value ?: 0.grams
                                        item {
                                            Surface(color = MaterialTheme.colorScheme.surface) {
                                                Box(
                                                    modifier =
                                                        Modifier.heightIn(min = 40.dp)
                                                            .clip(MaterialTheme.shapes.medium)
                                                            .background(
                                                                nutrientsPalette
                                                                    .carbohydratesOnSurfaceContainer
                                                                    .copy(alpha = .25f)
                                                            ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = proteins.stringResource(),
                                                        modifier =
                                                            Modifier.padding(horizontal = 16.dp),
                                                        color =
                                                            nutrientsPalette
                                                                .carbohydratesOnSurfaceContainer,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Surface(
                onClick = onSearchBar,
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shadowElevation = .5.dp,
            ) {
                SearchBarContent(
                    textFieldState = textFieldState,
                    homeProgress = homeProgress,
                    onBack = onBack,
                    onSearch = onSearch,
                    onClear = { onSearch(null) },
                    onBarcodeScanner = onBarcodeScanner,
                    modifier =
                        Modifier.sizeIn(
                                minWidth = 360.dp,
                                maxWidth = 720.dp,
                                minHeight = SearchBarDefaults.InputFieldHeight,
                                maxHeight = SearchBarDefaults.InputFieldHeight,
                            )
                            .padding(horizontal = 4.dp),
                )
            }
        }
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
    val showSlots by remember(progress) { derivedStateOf { progress() > 0f } }
    Layout(
        modifier = modifier.heightIn(min = 64.dp),
        content = {
            if (showSlots) Box(contentAlignment = Alignment.Center) { startSlot() }
            Box(contentAlignment = Alignment.Center) { content() }
            if (showSlots) Box(contentAlignment = Alignment.Center) { endSlot() }
        },
    ) { measurables, constraints ->
        val p = progress()
        val slotPx = lerp(0.dp, 48.dp, p).roundToPx().coerceAtLeast(0)
        val hPadPx = lerp(12.dp, 4.dp, p).roundToPx().coerceAtLeast(0)
        val gapPx = lerp(0.dp, 4.dp, p).roundToPx().coerceAtLeast(0)

        val startM = if (measurables.size == 3) measurables[0] else null
        val centerM = if (measurables.size == 3) measurables[1] else measurables[0]
        val endM = if (measurables.size == 3) measurables[2] else null

        val looseHeight = Constraints(minHeight = 0, maxHeight = constraints.maxHeight)

        val startP = startM?.measure(looseHeight.copy(minWidth = slotPx, maxWidth = slotPx))
        val endP = endM?.measure(looseHeight.copy(minWidth = slotPx, maxWidth = slotPx))

        val maxCenterWidth =
            (constraints.maxWidth - slotPx * 2 - hPadPx * 2 - gapPx * 2).coerceAtLeast(0)
        val centerP = centerM.measure(looseHeight.copy(minWidth = 0, maxWidth = maxCenterWidth))

        val contentHeight = maxOf(startP?.height ?: 0, endP?.height ?: 0, centerP.height)
        val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

        val neededWidth = slotPx * 2 + hPadPx * 2 + gapPx * 2 + centerP.width
        val totalWidth = neededWidth.coerceIn(constraints.minWidth, constraints.maxWidth)

        layout(totalWidth, height) {
            startP?.placeRelative(hPadPx, (height - startP.height) / 2)
            centerP.placeRelative(hPadPx + slotPx + gapPx, (height - centerP.height) / 2)
            endP?.placeRelative(totalWidth - hPadPx - slotPx, (height - endP.height) / 2)
        }
    }
}

@Composable
private fun SearchBarContent(
    textFieldState: TextFieldState,
    homeProgress: () -> Float,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isHome by remember(homeProgress) { derivedStateOf { homeProgress() == 1f } }

    Box(modifier) {
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
                    contentDescription =
                        org.jetbrains.compose.resources.stringResource(Res.string.action_search),
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
                    contentDescription =
                        org.jetbrains.compose.resources.stringResource(Res.string.action_close),
                )
            }
        }
        if (textFieldState.text.isEmpty() || isHome) {
            updateTransition(isHome, "placeholder").AnimatedContent(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                transitionSpec = { Crossfade.crossfade() },
                contentAlignment = Alignment.Center,
            ) {
                if (it)
                    Text(
                        text = org.jetbrains.compose.resources.stringResource(Res.string.app_name),
                        modifier = Modifier.wrapContentSize(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.brand.bodyLarge,
                        maxLines = 1,
                    )
                else
                    Text(
                        text =
                            org.jetbrains.compose.resources.stringResource(
                                Res.string.action_search_foods
                            ),
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
                        contentDescription =
                            org.jetbrains.compose.resources.stringResource(Res.string.action_clear),
                    )
                }
            }
            IconButton(onClick = onBarcodeScanner, shapes = IconButtonDefaults.shapes()) {
                Icon(
                    painter = painterResource(Res.drawable.ic_barcode_scanner),
                    contentDescription =
                        org.jetbrains.compose.resources.stringResource(
                            Res.string.action_scan_barcode
                        ),
                )
            }
        }
    }
}

@Composable
fun SearchInputField(
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
fun <T> Modifier.swipeThroughList(
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
