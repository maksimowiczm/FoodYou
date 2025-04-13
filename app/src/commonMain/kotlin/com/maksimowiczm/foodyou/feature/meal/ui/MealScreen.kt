package com.maksimowiczm.foodyou.feature.meal.ui

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.core.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.core.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.addfood.SearchSharedTransition
import com.maksimowiczm.foodyou.feature.addfood.ui.LocalAddFoodSharedTransitionScope
import com.maksimowiczm.foodyou.feature.meal.domain.MealFood
import com.maksimowiczm.foodyou.feature.meal.domain.MealWithFood
import com.maksimowiczm.foodyou.feature.meal.ui.MealCardTransitionSpecs.overlayClipFromScreenToCard
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun MealScreen(
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    mealId: Long,
    date: LocalDate,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditEntry: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealScreenViewModel = koinViewModel(
        parameters = { parametersOf(mealId, date) }
    )
) {
    val meal by viewModel.meal.collectAsStateWithLifecycle()

    when (val meal = meal) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> MealScreen(
            navigationScope = navigationScope,
            mealHeaderScope = mealHeaderScope,
            meal = meal,
            epochDay = date.toEpochDays(),
            deletedEntryChannel = viewModel.deletedMeasurement,
            onAddFood = onAddFood,
            onBarcodeScanner = onBarcodeScanner,
            onEditEntry = onEditEntry,
            onDeleteEntry = remember(viewModel) { viewModel::onDeleteMeasurement },
            onDeleteEntryUndo = remember(viewModel) { viewModel::onRestoreMeasurement },
            formatDate = remember(viewModel) { viewModel::formatDate },
            formatTime = remember(viewModel) { viewModel::formatTime },
            modifier = modifier
        )
    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
private fun MealScreen(
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    meal: MealWithFood,
    epochDay: Int,
    deletedEntryChannel: Flow<MeasurementId>,
    onAddFood: () -> Unit,
    onBarcodeScanner: () -> Unit,
    onEditEntry: (MeasurementId) -> Unit,
    onDeleteEntry: (MeasurementId) -> Unit,
    onDeleteEntryUndo: (MeasurementId) -> Unit,
    formatDate: (LocalDate) -> String,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val homeSTS =
        LocalHomeSharedTransitionScope.current ?: error("No home shared transition scope")
    val addFoodSTS =
        LocalAddFoodSharedTransitionScope.current ?: error("No add food shared transition scope")

    val topBar = @Composable {
        with(homeSTS) {
            TopBar(
                mealHeaderScope = mealHeaderScope,
                meal = meal,
                epochDay = epochDay,
                formatDate = formatDate,
                formatTime = formatTime,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealCardTransitionKeys.MealContainer(
                            mealId = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = mealHeaderScope,
                    enter = MealCardTransitionSpecs.containerEnterTransition,
                    exit = MealCardTransitionSpecs.containerExitTransition,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(
                        mealHeaderScope.overlayClipFromScreenToCard()
                    )
                )
            )
        }
    }

    // Flag on first scroll
    var scrolled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(lazyListState.isScrollInProgress) {
        scrolled = snapshotFlow { lazyListState.isScrollInProgress }.first { it }
    }

    val floatingActionButton = @Composable {
        with(addFoodSTS) {
            Column(
                modifier = Modifier.animateFloatingActionButton(
                    visible = !mealHeaderScope.transition.isRunning,
                    alignment = Alignment.BottomEnd
                ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(navigationScope) {
                    SmallFloatingActionButton(
                        onClick = onBarcodeScanner,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.animateEnterExit(
                            enter = SearchSharedTransition.smallFabEnterTransition
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                }

                FloatingActionButton(
                    onClick = onAddFood,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(
                            key = SearchSharedTransition.CONTAINER
                        ),
                        animatedVisibilityScope = navigationScope,
                        enter = SearchSharedTransition.fabContainerEnterTransition,
                        exit = SearchSharedTransition.fabContainerExitTransition,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )
                ) {
                    Box(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = SearchSharedTransition.CONTENT
                            ),
                            animatedVisibilityScope = navigationScope,
                            enter = SearchSharedTransition.fabContentEnterTransition,
                            exit = SearchSharedTransition.fabContentExitTransition,
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                        ),
                        contentAlignment = Alignment.Center
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
                                visible = !scrolled || !lazyListState.canScrollForward
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
    }

    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val selectedItem by remember(meal) {
        derivedStateOf {
            meal.foods.getOrNull(selectedIndex)
        }
    }

    when (val item = selectedItem) {
        is MealFood -> {
            val sheetState = rememberModalBottomSheetState()

            ModalBottomSheet(
                onDismissRequest = { selectedIndex = -1 },
                sheetState = sheetState
            ) {
                BottomSheetContent(
                    item = item,
                    onEditEntry = {
                        coroutineScope.launch {
                            sheetState.hide()
                            selectedIndex = -1
                            onEditEntry(item.measurementId)
                        }
                    },
                    onDeleteEntry = {
                        coroutineScope.launch {
                            sheetState.hide()
                            selectedIndex = -1
                            onDeleteEntry(item.measurementId)
                        }
                    }
                )
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val latestDeleteEntryUndo by rememberUpdatedState(onDeleteEntryUndo)
    val entryDeletedString = stringResource(Res.string.neutral_entry_deleted)
    val undoString = stringResource(Res.string.action_undo)
    LaunchedEffect(deletedEntryChannel) {
        deletedEntryChannel.collectLatest { id ->
            val result = snackbarHostState.showSnackbar(
                message = entryDeletedString,
                actionLabel = undoString,
                withDismissAction = true
            )

            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> latestDeleteEntryUndo(id)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (meal.foods.isEmpty()) {
                Text(
                    text = stringResource(Res.string.neutral_no_products_meal_screen),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.safeContentPadding().zIndex(1f).align(Alignment.Center)
                )
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = paddingValues
            ) {
                itemsIndexed(
                    items = meal.foods,
                    key = { _, model ->
                        model.measurementId.toString()
                    }
                ) { i, model ->
                    Column(
                        modifier = Modifier.animateItem()
                    ) {
                        if (i > 0) {
                            HorizontalDivider(Modifier.padding(horizontal = 48.dp))
                        }

                        model.ListItem(
                            modifier = Modifier.clickable {
                                selectedIndex = i
                            }
                        )
                    }
                }

                // FAB spacer
                item {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Spacer(Modifier.height(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Spacer(Modifier.height(56.dp))
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.TopBar(
    mealHeaderScope: AnimatedVisibilityScope,
    meal: MealWithFood,
    epochDay: Int,
    formatDate: (LocalDate) -> String,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
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
                animatedVisibilityScope = mealHeaderScope
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
                animatedVisibilityScope = mealHeaderScope
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
                    text = remember(enDash, meal, formatTime) {
                        buildString {
                            append(formatTime(meal.from))
                            append(" ")
                            append(enDash)
                            append(" ")
                            append(formatTime(meal.to))
                        }
                    },
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    val caloriesLabel = @Composable {
        Text(
            text = if (meal.isEmpty) {
                stringResource(Res.string.em_dash)
            } else {
                meal.calories.toString()
            }
        )
    }
    val proteinsLabel = @Composable {
        Text(
            text = if (meal.isEmpty) {
                stringResource(Res.string.em_dash)
            } else {
                "${meal.proteins} " + stringResource(Res.string.unit_gram_short)
            }
        )
    }
    val carbohydratesLabel = @Composable {
        Text(
            text = if (meal.isEmpty) {
                stringResource(Res.string.em_dash)
            } else {
                "${meal.carbohydrates} " + stringResource(Res.string.unit_gram_short)
            }
        )
    }
    val fatsLabel = @Composable {
        Text(
            text = if (meal.isEmpty) {
                stringResource(Res.string.em_dash)
            } else {
                "${meal.fats} " + stringResource(Res.string.unit_gram_short)
            }
        )
    }

    Surface(
        modifier = modifier,
        color = headerColor
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(insets.asPaddingValues())
                .consumeWindowInsets(insets)
        ) {
            with(mealHeaderScope) {
                Text(
                    text = formatDate(meal.date),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.animateEnterExit(
                        enter = fadeIn(
                            tween(
                                delayMillis = DefaultDurationMillis
                            )
                        ),
                        exit = fadeOut(tween(50))
                    )
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
                            animatedVisibilityScope = mealHeaderScope
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    item: MealFood,
    onEditEntry: () -> Unit,
    onDeleteEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDeleteEntry = {
                onDeleteEntry()
                showDeleteDialog = false
            }
        )
    }

    Column(
        modifier = modifier
    ) {
        item.ListItem()
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        ListItem(
            headlineContent = {
                Text(stringResource(Res.string.action_edit_entry))
            },
            modifier = Modifier.clickable { onEditEntry() },
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
private fun MealFood.ListItem(modifier: Modifier = Modifier) {
    val weight = weight
    val measurementString = measurementString
    val caloriesString = caloriesString
    if (weight == null || measurementString == null || caloriesString == null) {
        // TODO handle broken weight
        return
    }

    ListItem(
        headlineContent = { Text(food.name) },
        modifier = modifier,
        overlineContent = food.brand?.let { { Text(it) } },
        supportingContent = {
            Column {
                val proteins = (food.nutrients.proteins.value * weight / 100f).roundToInt()
                val carbohydrates =
                    (food.nutrients.carbohydrates.value * weight / 100f).roundToInt()
                val fats = (food.nutrients.fats.value * weight / 100f).roundToInt()

                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )
                MeasurementSummary(
                    measurementString = measurementString,
                    measurementStringShort = measurementStringShort,
                    caloriesString = caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

private val MealFood.measurementStringShort: String
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

private val MealFood.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null

        return when (measurement) {
            is Measurement.Gram -> short
            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight ${stringResource(Res.string.unit_gram_short)})"
        }
    }

private val MealFood.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * food.nutrients.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }
