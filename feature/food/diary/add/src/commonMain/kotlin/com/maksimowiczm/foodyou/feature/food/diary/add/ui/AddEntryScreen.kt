package com.maksimowiczm.foodyou.feature.food.diary.add.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallSplit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.AddEntryEvent
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.AddEntryViewModel
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.FoodModel
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.ProductModel
import com.maksimowiczm.foodyou.feature.food.diary.add.presentation.RecipeModel
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.ChipsDatePicker
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.ChipsMealPicker
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.FoodMeasurementFormState
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.Source
import com.maksimowiczm.foodyou.feature.food.diary.shared.ui.rememberFoodMeasurementFormState
import com.maksimowiczm.foodyou.feature.food.shared.ui.MeasurementPicker
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.ext.add
import com.maksimowiczm.foodyou.shared.ui.ext.minus
import com.maksimowiczm.foodyou.shared.ui.ext.plus
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddEntryScreen(
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onEntryAdded: () -> Unit,
    onFoodDeleted: () -> Unit,
    onIngredient: (FoodId, Measurement) -> Unit,
    foodId: FoodId,
    mealId: Long,
    date: LocalDate,
    measurement: Measurement?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val viewModel: AddEntryViewModel = koinViewModel { parametersOf(foodId) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            is AddEntryEvent.FoodDeleted -> onFoodDeleted()
            is AddEntryEvent.EntryAdded -> onEntryAdded()
        }
    }

    val food = viewModel.food.collectAsStateWithLifecycle().value
    val events by viewModel.foodHistory.collectAsStateWithLifecycle()
    val meals = viewModel.meals.collectAsStateWithLifecycle().value
    val today by viewModel.today.collectAsStateWithLifecycle()
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle().value
    val possibleTypes = viewModel.possibleMeasurementTypes.collectAsStateWithLifecycle().value
    val measurementSuggestion by viewModel.suggestedMeasurement.collectAsStateWithLifecycle()

    val selectedMeasurement =
        remember(measurement, measurementSuggestion) { measurement ?: measurementSuggestion }

    if (
        food == null ||
            meals == null ||
            suggestions == null ||
            possibleTypes == null ||
            selectedMeasurement == null
    ) {
        // TODO loading state
    } else {
        val state =
            rememberFoodMeasurementFormState(
                today = today,
                possibleDates =
                    listOf(today.minus(1.days), today, today.plus(1.days), date)
                        .distinct()
                        .sorted(),
                selectedDate = date,
                meals = meals.map { it.name },
                selectedMeal =
                    remember(meals, mealId) {
                            meals.firstOrNull { it.id == mealId } ?: meals.firstOrNull()
                        }
                        ?.name,
                suggestions = suggestions,
                possibleTypes = possibleTypes,
                selectedMeasurement = selectedMeasurement,
            )

        AddEntryScreen(
            onBack = onBack,
            onAdd = {
                val selectedMealId =
                    state.mealsState.selectedMeal?.let { mealName ->
                        meals.firstOrNull { it.name == mealName }?.id
                    }

                if (selectedMealId != null) {
                    viewModel.addEntry(
                        measurement = state.measurementState.measurement,
                        mealId = selectedMealId,
                        date = state.dateState.selectedDate,
                    )
                }
            },
            onUnpack = {
                val selectedMealId =
                    state.mealsState.selectedMeal?.let { mealName ->
                        meals.firstOrNull { it.name == mealName }?.id
                    }

                if (selectedMealId != null) {
                    viewModel.unpack(
                        measurement = state.measurementState.measurement,
                        mealId = selectedMealId,
                        date = state.dateState.selectedDate,
                    )
                }
            },
            onEditFood = onEditFood,
            onDelete = viewModel::deleteFood,
            onIngredient = onIngredient,
            food = food,
            history = events,
            state = state,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onUnpack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onDelete: () -> Unit,
    onIngredient: (FoodId, Measurement) -> Unit,
    food: FoodModel,
    history: List<FoodHistory>,
    state: FoodMeasurementFormState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val topBar =
        @Composable {
            MediumTopAppBar(
                title = { Text(food.name) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = { Menu(onEdit = { onEditFood(food.foodId) }, onDelete = onDelete) },
                scrollBehavior = scrollBehavior,
            )
        }

    val fab =
        @Composable {
            Column(
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = !animatedVisibilityScope.transition.isRunning && state.isValid,
                        alignment = Alignment.BottomEnd,
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (food.canUnpack) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (state.isValid) {
                                onUnpack()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.CallSplit,
                                contentDescription = null,
                            )
                        },
                        text = { Text(stringResource(Res.string.action_unpack)) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                LargeExtendedFloatingActionButton(
                    onClick = {
                        if (state.isValid) {
                            onAdd()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(Res.string.action_save),
                            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                        )
                    },
                    text = { Text(stringResource(Res.string.action_save)) },
                )
            }
        }

    Scaffold(modifier = modifier, topBar = topBar, floatingActionButton = fab) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding =
                paddingValues.add(vertical = 8.dp).let {
                    if (food.canUnpack) {
                        it.add(bottom = 8.dp + 56.dp + 8.dp + 80.dp + 24.dp) // Double FAB
                    } else {
                        it.add(bottom = 80.dp + 24.dp) // FAB
                    }
                },
        ) {
            item { HorizontalDivider(Modifier.padding(horizontal = 8.dp)) }

            item {
                ChipsDatePicker(state = state.dateState, modifier = Modifier.padding(8.dp))
                HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                ChipsMealPicker(state = state.mealsState, modifier = Modifier.padding(8.dp))
                HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                MeasurementPicker(state = state.measurementState, modifier = Modifier.padding(8.dp))
            }

            if (food is RecipeModel) {
                item {
                    val measurement = state.measurementState.measurement
                    val ingredients = food.unpack(food.weight(measurement))

                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    Ingredients(
                        ingredients = ingredients,
                        onIngredient = onIngredient,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            item {
                HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                NutrientList(
                    food = food,
                    measurement = state.measurementState.measurement,
                    onEditFood = onEditFood,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            val note = food.note
            if (note != null) {
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(Res.string.headline_note),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(text = note, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (food is ProductModel) {
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(Res.string.headline_source),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Source(food.source)
                    }
                }
            }

            if (history.isNotEmpty()) {
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    FoodHistory(events = history, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Menu(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(onDismissRequest = { showDeleteDialog = false }, onDelete = onDelete)
    }

    Box(modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.action_show_more),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_edit)) },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.action_delete)) },
                onClick = {
                    expanded = false
                    showDeleteDialog = true
                },
            )
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDelete) { Text(stringResource(Res.string.action_delete)) }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) },
        title = { Text(stringResource(Res.string.headline_delete_food)) },
        text = { Text(stringResource(Res.string.description_delete_food)) },
    )
}
