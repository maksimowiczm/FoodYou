package com.maksimowiczm.foodyou.feature.meal.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Meal
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.sumOf
import com.maksimowiczm.foodyou.core.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.core.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealCardTransitionKeys
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealCardTransitionSpecs
import com.maksimowiczm.foodyou.feature.meal.ui.card.MealCardTransitionSpecs.overlayClipFromScreenToCard
import com.maksimowiczm.foodyou.feature.meal.ui.component.MealHeader
import com.maksimowiczm.foodyou.feature.meal.ui.component.NutrientsLayout
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * @param screenSts scope for the screen transition
 * @param enterSTS scope for the enter transition
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@JvmName("NullableMealScreen")
@Composable
fun MealScreen(
    screenSts: SharedTransitionScope,
    screenScope: AnimatedVisibilityScope,
    enterSTS: SharedTransitionScope,
    enterScope: AnimatedVisibilityScope,
    date: LocalDate,
    meal: Meal?,
    foods: List<FoodWithMeasurement>?,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditMeasurement: (MeasurementId) -> Unit,
    onDeleteEntry: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier
) {
    if (meal == null || foods == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
    } else {
        MealScreen(
            screenSts = screenSts,
            screenScope = screenScope,
            enterSTS = enterSTS,
            enterScope = enterScope,
            date = date,
            meal = meal,
            foods = foods,
            onAddFood = onAddFood,
            onBarcodeScanner = onBarcodeScanner,
            onEditMeasurement = onEditMeasurement,
            onDeleteEntry = onDeleteEntry,
            modifier = modifier
        )
    }
}

/**
 * @param screenSts scope for the screen transition
 * @param enterSTS scope for the enter transition
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MealScreen(
    screenSts: SharedTransitionScope,
    screenScope: AnimatedVisibilityScope,
    enterSTS: SharedTransitionScope,
    enterScope: AnimatedVisibilityScope,
    date: LocalDate,
    meal: Meal,
    foods: List<FoodWithMeasurement>,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditMeasurement: (MeasurementId) -> Unit,
    onDeleteEntry: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val topBar = @Composable {
        with(enterSTS) {
            MealScreenTopBar(
                enterScope = enterScope,
                meal = meal,
                foods = foods,
                date = date,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealCardTransitionKeys.MealContainer(
                            mealId = meal.id,
                            epochDay = date.toEpochDays()
                        )
                    ),
                    animatedVisibilityScope = enterScope,
                    enter = MealCardTransitionSpecs.containerEnterTransition,
                    exit = MealCardTransitionSpecs.containerExitTransition,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(
                        enterScope.overlayClipFromScreenToCard()
                    )
                )
            )
        }
    }

    var fabHeight by remember { mutableIntStateOf(0) }
    val fab = @Composable {
        // Flag on first scroll
        var scrolled by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(listState.isScrollInProgress) {
            scrolled = snapshotFlow { listState.isScrollInProgress }.first { it }
        }

        with(screenSts) {
            MealScreenFloatingActionButton(
                screenScope = screenScope,
                onAddFood = onAddFood,
                onBarcodeScanner = onBarcodeScanner,
                expanded = !scrolled || !listState.canScrollForward,
                modifier = Modifier
                    .animateFloatingActionButton(
                        visible = !enterScope.transition.isRunning,
                        alignment = Alignment.BottomEnd
                    )
                    .onGloballyPositioned { fabHeight = it.size.height }
            )
        }
    }

    var selectedIndex by rememberSaveable { mutableStateOf(-1) }
    val selectedItem = remember(foods, selectedIndex) {
        foods.getOrNull(selectedIndex)
    }
    if (selectedItem != null) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = coroutineScope.lambda {
                sheetState.hide()
                selectedIndex = -1
            },
            sheetState = sheetState,
            modifier = modifier.testTag(MealScreenTestTags.BOTTOM_SHEET)
        ) {
            BottomSheetContent(
                food = selectedItem,
                onEdit = coroutineScope.lambda {
                    sheetState.hide()
                    onEditMeasurement(selectedItem.measurementId)
                    selectedIndex = -1
                },
                onDelete = coroutineScope.lambda {
                    sheetState.hide()
                    onDeleteEntry(selectedItem.measurementId)
                    selectedIndex = -1
                }
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = fab
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            contentPadding = paddingValues.add(
                PaddingValues(
                    bottom = density.run { fabHeight.toDp() } + 8.dp
                )
            )
        ) {
            itemsIndexed(
                items = foods,
                key = { _, it -> it.measurementId.toString() }
            ) { i, food ->
                Column(
                    modifier = Modifier.animateItem()
                ) {
                    if (i > 0) {
                        HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                    }

                    FoodListItem(
                        food = food,
                        modifier = Modifier
                            .testTag(MealScreenTestTags.FoodItem(food.food.id).toString())
                            .clickable { selectedIndex = i }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MealScreenTopBar(
    enterScope: AnimatedVisibilityScope,
    meal: Meal,
    foods: List<FoodWithMeasurement>,
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalDateFormatter.current
    val epochDay = date.toEpochDays()

    val insets = TopAppBarDefaults.windowInsets
    val headerColor = MealCardTransitionSpecs.containerColor

    val headline = @Composable {
        Text(
            text = meal.name,
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = MealCardTransitionKeys.MealTitle(
                        mealId = meal.id,
                        epochDay = epochDay
                    )
                ),
                animatedVisibilityScope = enterScope
            )
        )
    }
    val time = @Composable {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(
                    key = MealCardTransitionKeys.MealTime(
                        mealId = meal.id,
                        epochDay = epochDay
                    )
                ),
                animatedVisibilityScope = enterScope
            )
        ) {
            if (meal.isAllDay) {
                Text(
                    text = stringResource(Res.string.headline_all_day),
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                val enDash = stringResource(Res.string.en_dash)

                Text(
                    text = remember(enDash, meal, dateFormatter) {
                        buildString {
                            append(dateFormatter.formatTime(meal.from))
                            append(" $enDash ")
                            append(dateFormatter.formatTime(meal.to))
                        }
                    },
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    val caloriesLabel = @Composable {
        val calories = foods
            .sumOf { it.food.nutrients.calories.value * it.weight!! / 100f }
            .roundToInt()

        Text(
            text = if (foods.isEmpty()) {
                stringResource(Res.string.em_dash)
            } else {
                calories.toString()
            }
        )
    }

    val proteinsLabel = @Composable {
        val proteins = foods
            .sumOf { it.food.nutrients.proteins.value * it.weight!! / 100f }
            .roundToInt()

        Text(
            text = if (foods.isEmpty()) {
                stringResource(Res.string.em_dash)
            } else {
                "$proteins " + stringResource(Res.string.unit_gram_short)
            }
        )
    }

    val carbohydratesLabel = @Composable {
        val carbohydrates = foods
            .sumOf { it.food.nutrients.carbohydrates.value * it.weight!! / 100f }
            .roundToInt()

        Text(
            text = if (foods.isEmpty()) {
                stringResource(Res.string.em_dash)
            } else {
                "$carbohydrates " + stringResource(Res.string.unit_gram_short)
            }
        )
    }

    val fatsLabel = @Composable {
        val fats = foods
            .sumOf { it.food.nutrients.fats.value * it.weight!! / 100f }
            .roundToInt()

        Text(
            text = if (foods.isEmpty()) {
                stringResource(Res.string.em_dash)
            } else {
                "$fats " + stringResource(Res.string.unit_gram_short)
            }
        )
    }

    Surface(
        modifier = modifier,
        color = headerColor
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(insets)
                .consumeWindowInsets(insets)
                .padding(16.dp)
        ) {
            with(enterScope) {
                Text(
                    text = dateFormatter.formatDate(date),
                    modifier = Modifier.animateEnterExit(
                        enter = fadeIn(
                            tween(
                                delayMillis = DefaultDurationMillis
                            )
                        ),
                        exit = fadeOut(tween(50))
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            MealHeader(
                headline = headline,
                time = time,
                spacer = { Spacer(Modifier.height(16.dp)) },
                nutrientsLayout = {
                    NutrientsLayout(
                        caloriesLabel = caloriesLabel,
                        proteinsLabel = proteinsLabel,
                        carbohydratesLabel = carbohydratesLabel,
                        fatsLabel = fatsLabel,
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = MealCardTransitionKeys.MealNutrients(
                                    mealId = meal.id,
                                    epochDay = epochDay
                                )
                            ),
                            animatedVisibilityScope = enterScope
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MealScreenFloatingActionButton(
    screenScope: AnimatedVisibilityScope,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        with(screenScope) {
            SmallFloatingActionButton(
                onClick = onBarcodeScanner,
                modifier = Modifier
                    .testTag(MealScreenTestTags.BARCODE_SCANNER_FAB)
                    .animateEnterExit(
                        enter = MealScreenSharedTransition.smallFabEnterTransition
                    ),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = stringResource(Res.string.action_scan_barcode)
                )
            }
        }

        FloatingActionButton(
            onClick = onAddFood,
            modifier = Modifier
                .testTag(MealScreenTestTags.ADD_FOOD_FAB)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealScreenSharedTransition.FAB_CONTAINER
                    ),
                    animatedVisibilityScope = screenScope,
                    enter = MealScreenSharedTransition.fabContainerEnterTransition,
                    exit = MealScreenSharedTransition.fabContainerExitTransition,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealScreenSharedTransition.FAB_CONTENT
                    ),
                    animatedVisibilityScope = screenScope,
                    enter = MealScreenSharedTransition.fabContentEnterTransition,
                    exit = MealScreenSharedTransition.fabContentExitTransition,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    AnimatedVisibility(
                        visible = expanded
                    ) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.action_add_food)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetContent(
    food: FoodWithMeasurement,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDeleteEntry = {
                onDelete()
                showDeleteDialog = false
            }
        )
    }

    Column(
        modifier = modifier
    ) {
        FoodListItem(food)
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_edit_entry))
            },
            modifier = Modifier.clickable { onEdit() },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_delete_entry))
            },
            modifier = Modifier.clickable { showDeleteDialog = true },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun DeleteDialog(onDismissRequest: () -> Unit, onDeleteEntry: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDeleteEntry
            ) {
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(Res.string.action_delete_entry))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product_entry))
        }
    )
}

@Composable
private fun FoodListItem(food: FoodWithMeasurement, modifier: Modifier = Modifier) {
    val nutrients = food.food.nutrients
    val weight = food.weight!!

    ListItem(
        headlineContent = { Text(food.food.name) },
        modifier = modifier,
        supportingContent = {
            Column {
                val proteins = (nutrients.proteins.value * weight / 100f).roundToInt()
                val carbohydrates =
                    (nutrients.carbohydrates.value * weight / 100f).roundToInt()
                val fats = (nutrients.fats.value * weight / 100f).roundToInt()

                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )
                MeasurementSummary(
                    measurementString = food.measurementString,
                    measurementStringShort = food.measurementStringShort,
                    caloriesString = food.caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        overlineContent = food.food.brand?.let { { Text(it) } },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

private val FoodWithMeasurement.measurementStringShort: String
    @Composable get() = with(measurement) {
        when (this) {
            is Measurement.Package -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )

            is Measurement.Serving -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )

            is Measurement.Gram -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_gram_short)
        }
    }

private val FoodWithMeasurement.measurementString: String
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: error("Food weight is unknown")

        return when (measurement) {
            is Measurement.Gram -> short
            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight ${stringResource(Res.string.unit_gram_short)})"
        }
    }

private val FoodWithMeasurement.caloriesString: String
    @Composable get() = weight?.let {
        val value = (it * food.nutrients.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    } ?: error("Food weight is unknown")
