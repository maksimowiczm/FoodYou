package com.maksimowiczm.foodyou.feature.diary.ui.meal.compose

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.LocalMealSharedTransitionScope
import com.maksimowiczm.foodyou.feature.diary.ui.SearchSharedTransition
import com.maksimowiczm.foodyou.feature.diary.ui.component.MealHeader
import com.maksimowiczm.foodyou.feature.diary.ui.component.MealHeaderTransitionKeys
import com.maksimowiczm.foodyou.feature.diary.ui.component.MealHeaderTransitionSpecs
import com.maksimowiczm.foodyou.feature.diary.ui.component.MealHeaderTransitionSpecs.overlayClipFromScreenToCard
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsLayout
import com.maksimowiczm.foodyou.feature.diary.ui.meal.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.Meal
import com.maksimowiczm.foodyou.feature.diary.ui.meal.model.MealFoodListItem
import com.maksimowiczm.foodyou.ui.LocalHomeSharedTransitionScope
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DiaryDayMealScreen(
    date: LocalDate,
    mealId: Long,
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    onProductAdd: () -> Unit,
    onBarcodeScan: () -> Unit,
    onEditEntry: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryDayMealViewModel = koinViewModel(
        parameters = { parametersOf(mealId, date) }
    )
) {
    val meal by viewModel.meal.collectAsStateWithLifecycle(null)

    @Suppress("NAME_SHADOWING")
    when (val meal = meal) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> {
            DiaryDayMealScreen(
                navigationScope = navigationScope,
                mealHeaderScope = mealHeaderScope,
                meal = meal,
                epochDay = date.toEpochDays(),
                deletedEntryChannel = viewModel.deleteEvent,
                onProductAdd = onProductAdd,
                onBarcodeScan = onBarcodeScan,
                onEditEntry = onEditEntry,
                onDeleteEntry = viewModel::onDeleteEntry,
                onDeleteEntryUndo = viewModel::onDeleteEntryUndo,
                modifier = modifier
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
private fun DiaryDayMealScreen(
    navigationScope: AnimatedVisibilityScope,
    mealHeaderScope: AnimatedVisibilityScope,
    meal: Meal,
    epochDay: Int,
    deletedEntryChannel: Flow<MeasurementId>,
    onProductAdd: () -> Unit,
    onBarcodeScan: () -> Unit,
    onEditEntry: (MeasurementId) -> Unit,
    onDeleteEntry: (MeasurementId) -> Unit,
    onDeleteEntryUndo: (MeasurementId) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeSTS =
        LocalHomeSharedTransitionScope.current ?: error("No shared transition scope")
    val mealSTS =
        LocalMealSharedTransitionScope.current ?: error("No shared transition scope")

    val headerColor = MealHeaderTransitionSpecs.containerColor
    val containerColor = MaterialTheme.colorScheme.surface

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    // Flag on first scroll
    var scrolled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(lazyListState.isScrollInProgress) {
        scrolled = snapshotFlow { lazyListState.isScrollInProgress }.first { it }
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

    var selectedIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val modalSheetState = rememberModalBottomSheetState()

    val selectedProduct = selectedIndex?.let { meal.foods.getOrNull(it) }

    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedIndex = null },
            sheetState = modalSheetState
        ) {
            ModalSheetContent(
                model = selectedProduct,
                onEditEntry = {
                    coroutineScope.launch {
                        onEditEntry(selectedProduct.measurementId)
                        modalSheetState.hide()
                        selectedIndex = null
                    }
                },
                onDeleteEntry = {
                    coroutineScope.launch {
                        onDeleteEntry(selectedProduct.measurementId)
                        modalSheetState.hide()
                        selectedIndex = null
                    }
                }
            )
        }
    }

    val topBar = @Composable {
        val insets = TopAppBarDefaults.windowInsets

        with(homeSTS) {
            Surface(
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = MealHeaderTransitionKeys.MealContainer(
                            mealId = meal.id,
                            epochDay = epochDay
                        )
                    ),
                    animatedVisibilityScope = mealHeaderScope,
                    enter = MealHeaderTransitionSpecs.containerEnterTransition,
                    exit = MealHeaderTransitionSpecs.containerExitTransition,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(
                        mealHeaderScope.overlayClipFromScreenToCard()
                    )
                ),
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
                            text = meal.date,
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

                    val headline = @Composable {
                        Text(
                            text = meal.name,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    key = MealHeaderTransitionKeys.MealTitle(
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
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    key = MealHeaderTransitionKeys.MealTime(
                                        mealId = meal.id,
                                        epochDay = epochDay
                                    )
                                ),
                                animatedVisibilityScope = mealHeaderScope
                            )
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colorScheme.outline,
                                LocalTextStyle provides MaterialTheme.typography.bodyLarge
                            ) {
                                if (meal.isAllDay) {
                                    Text(
                                        text = stringResource(Res.string.headline_all_day)
                                    )
                                } else {
                                    Text(
                                        text = meal.from
                                    )
                                    Text(
                                        text = stringResource(Res.string.en_dash)
                                    )
                                    Text(
                                        text = meal.to
                                    )
                                }
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
                                meal.proteins.toString() + " " +
                                    stringResource(Res.string.unit_gram_short)
                            }
                        )
                    }
                    val carbohydratesLabel = @Composable {
                        Text(
                            text = if (meal.isEmpty) {
                                stringResource(Res.string.em_dash)
                            } else {
                                meal.carbohydrates.toString() + " " +
                                    stringResource(Res.string.unit_gram_short)
                            }
                        )
                    }
                    val fatsLabel = @Composable {
                        Text(
                            text = if (meal.isEmpty) {
                                stringResource(Res.string.em_dash)
                            } else {
                                meal.fats.toString() + " " +
                                    stringResource(Res.string.unit_gram_short)
                            }
                        )
                    }

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
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(
                                        key = MealHeaderTransitionKeys.MealNutrients(
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
    }

    // Can't use animatedVisibilityScope.transition.isRunning because it is set to false after
    // predictive back cancel 💀
    var fabVisible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        fabVisible = true
    }

    val floatingActionButton = @Composable {
        with(mealSTS) {
            Column(
                modifier = Modifier.animateFloatingActionButton(
                    visible = fabVisible,
                    alignment = Alignment.BottomEnd
                ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(navigationScope) {
                    SmallFloatingActionButton(
                        onClick = onBarcodeScan,
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
                    onClick = onProductAdd,
                    modifier = Modifier
                        .sharedBounds(
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

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        containerColor = containerColor
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (meal.foods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .padding(horizontal = 16.dp)
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.neutral_no_products_meal_screen),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
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

                        model.MealFoodListItem(
                            onClick = { selectedIndex = i }
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

@Composable
private fun ModalSheetContent(
    model: MealFoodListItem,
    onEditEntry: () -> Unit,
    onDeleteEntry: () -> Unit
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

    Column {
        model.MealFoodListItem()
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
