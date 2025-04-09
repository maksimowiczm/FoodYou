package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.core.ui.component.NutrientsList
import com.maksimowiczm.foodyou.feature.measurement.domain.MeasurableFood
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MeasurementScreen(
    viewModel: MeasurementScreenViewModel,
    onBack: () -> Unit,
    onCreateMeasurement: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDeleteFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    val food by viewModel.food.collectAsStateWithLifecycle()
    val selectedMeasurement by viewModel.selectedMeasurement.collectAsStateWithLifecycle()

    val onCreateMeasurement by rememberUpdatedState(onCreateMeasurement)
    val onDeleteFood by rememberUpdatedState(onDeleteFood)

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventBus.collectLatest { event ->
                when (event) {
                    MeasurementScreenEvent.Closed -> onCreateMeasurement()
                    MeasurementScreenEvent.FoodDeleted -> onDeleteFood()
                }
            }
        }
    }

    // TODO shimmer
    when (val food = food) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> MeasurementScreen(
            food = food,
            selectedMeasurement = food.selected ?: selectedMeasurement,
            onBack = onBack,
            onMeasurement = remember(viewModel) { viewModel::onConfirm },
            onEditFood = remember(food, onEditFood) { { onEditFood(food.food.id) } },
            onDeleteFood = remember(viewModel, food) { { viewModel.onDeleteFood(food.food.id) } },
            modifier = modifier
        )
    }
}

// TODO this probably need some refactoring. Too much bloat
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeasurementScreen(
    food: MeasurableFood,
    selectedMeasurement: Measurement?,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: () -> Unit,
    onDeleteFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDeleteFood
        )
    }

    // Fade in top app bar title when scrolling down
    val lazyListState = rememberLazyListState()
    var headlineHeight by remember { mutableIntStateOf(0) }
    var headlineAlpha by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collectLatest {
            headlineAlpha = if (lazyListState.firstVisibleItemIndex != 0) {
                1f
            } else {
                lerp(0f, 1f, it / headlineHeight.toFloat())
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topBar = @Composable {
        TopAppBar(
            title = {
                Text(
                    text = food.food.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.graphicsLayer { alpha = headlineAlpha }
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_go_back)
                    )
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        )
    }

    val packageInputState = run {
        val measurement =
            food.suggestions.firstOrNull { it is Measurement.Package } as? Measurement.Package

        if (measurement != null) {
            rememberMeasurementInputState(measurement)
        } else {
            null
        }
    }

    val servingInputState = run {
        val measurement =
            food.suggestions.firstOrNull { it is Measurement.Serving } as? Measurement.Serving

        if (measurement != null) {
            rememberMeasurementInputState(measurement)
        } else {
            null
        }
    }

    val gramInputState = run {
        val measurement =
            food.suggestions.firstOrNull { it is Measurement.Gram } as? Measurement.Gram

        if (measurement != null) {
            rememberMeasurementInputState(measurement)
        } else {
            null
        }
    }

    var extraFilter by rememberSaveable(
        stateSaver = Measurement.Saver
    ) { mutableStateOf<Measurement?>(null) }
    val chipsState = rememberWeightChipsState(food.food, extraFilter)
    LaunchedEffect(packageInputState, servingInputState, gramInputState) {
        merge(
            snapshotFlow { packageInputState?.formField?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Package(it) },
            snapshotFlow { servingInputState?.formField?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Serving(it) },
            snapshotFlow { gramInputState?.formField?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .map { Measurement.Gram(it) }
        ).collectLatest {
            extraFilter = it
        }
    }

    Scaffold(
        topBar = topBar,
        modifier = modifier.imePadding()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = food.food.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .onSizeChanged { headlineHeight = it.height }
                        .graphicsLayer { alpha = 1 - headlineAlpha }
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                val food = food.food
                val packageWeight = food.packageWeight

                val defaultColors = MeasurementInputDefaults.colors()
                val highlightContainerColor = MaterialTheme.colorScheme.primaryContainer
                val highlightContentColor = MaterialTheme.colorScheme.onPrimaryContainer

                if (packageWeight != null && packageInputState != null) {
                    val containerColor by animateColorAsState(
                        targetValue = if (packageInputState.measurement == selectedMeasurement) {
                            highlightContainerColor
                        } else {
                            defaultColors.containerColor
                        }
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (packageInputState.measurement == selectedMeasurement) {
                            highlightContentColor
                        } else {
                            defaultColors.contentColor
                        }
                    )

                    Column {
                        PackageMeasurementInput(
                            onConfirm = onMeasurement,
                            getWeight = { it.weight(packageWeight) },
                            getCalories = {
                                it.weight(packageWeight) * food.nutrients.calories.value /
                                    100f
                            },
                            formField = packageInputState.formField,
                            contentPadding = PaddingValues(8.dp),
                            colors = MeasurementInputDefaults.colors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            )
                        )
                        HorizontalDivider()
                    }
                }
            }

            item {
                val food = food.food
                val servingWeight = food.servingWeight

                val defaultColors = MeasurementInputDefaults.colors()
                val highlightContainerColor = MaterialTheme.colorScheme.primaryContainer
                val highlightContentColor = MaterialTheme.colorScheme.onPrimaryContainer

                if (servingWeight != null && servingInputState != null) {
                    val containerColor by animateColorAsState(
                        targetValue = if (servingInputState.measurement == selectedMeasurement) {
                            highlightContainerColor
                        } else {
                            defaultColors.containerColor
                        }
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (servingInputState.measurement == selectedMeasurement) {
                            highlightContentColor
                        } else {
                            defaultColors.contentColor
                        }
                    )

                    Column {
                        ServingMeasurementInput(
                            onConfirm = onMeasurement,
                            getWeight = { it.weight(servingWeight) },
                            getCalories = {
                                it.weight(servingWeight) * food.nutrients.calories.value /
                                    100f
                            },
                            formField = servingInputState.formField,
                            contentPadding = PaddingValues(8.dp),
                            colors = MeasurementInputDefaults.colors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            )
                        )
                        HorizontalDivider()
                    }
                }
            }

            item {
                val food = food.food

                val defaultColors = MeasurementInputDefaults.colors()
                val highlightContainerColor = MaterialTheme.colorScheme.primaryContainer
                val highlightContentColor = MaterialTheme.colorScheme.onPrimaryContainer

                if (gramInputState != null) {
                    val containerColor by animateColorAsState(
                        targetValue = if (gramInputState.measurement == selectedMeasurement) {
                            highlightContainerColor
                        } else {
                            defaultColors.containerColor
                        }
                    )

                    val contentColor by animateColorAsState(
                        targetValue = if (gramInputState.measurement == selectedMeasurement) {
                            highlightContentColor
                        } else {
                            defaultColors.contentColor
                        }
                    )

                    Column {
                        GramMeasurementInput(
                            onConfirm = onMeasurement,
                            getCalories = { it.value * food.nutrients.calories.value / 100f },
                            formField = gramInputState.formField,
                            contentPadding = PaddingValues(8.dp),
                            colors = MeasurementInputDefaults.colors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            )
                        )
                        HorizontalDivider()
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_nutrients),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                WeightChips(
                    state = chipsState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val nutrients = food.food.nutrients

                CaloriesProgressIndicator(
                    proteins = nutrients.proteins.value,
                    carbohydrates = nutrients.carbohydrates.value,
                    fats = nutrients.fats.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val nutrients by remember(chipsState.selectedFilter, food) {
                    derivedStateOf {
                        val weight = chipsState.selectedFilter.weight(food.food) ?: 100f
                        food.food.nutrients * weight / 100f
                    }
                }

                NutrientsList(
                    nutrients = nutrients,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Button(
                        onClick = onEditFood
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_edit))
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_delete))
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DeleteDialog(onDismissRequest: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
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
            Text(stringResource(Res.string.headline_delete_product))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product))
        }
    )
}
