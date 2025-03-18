package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.LocalTimeInput
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsScreenViewModel = koinViewModel()
) {
    val sortedMeals by viewModel.sortedMeals.collectAsStateWithLifecycle()

    MealsSettingsScreen(
        meals = sortedMeals,
        formatTime = viewModel::formatTime,
        onBack = onBack,
        onCreateMeal = viewModel::createMeal,
        onUpdateMeal = viewModel::updateMeal,
        onDeleteMeal = viewModel::deleteMeal,
        onSaveMealOrder = viewModel::orderMeals,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsSettingsScreen(
    meals: List<Meal>,
    formatTime: (LocalTime) -> String,
    onBack: () -> Unit,
    onCreateMeal: (String, LocalTime, LocalTime) -> Unit,
    onUpdateMeal: (Meal) -> Unit,
    onDeleteMeal: (Meal) -> Unit,
    onSaveMealOrder: (List<Meal>) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardStates by rememberMealsSettingsCardStates(meals)

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        cardStates = cardStates.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    var isReordering by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.headline_meals)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                actions = {
                    if (isReordering) {
                        IconButton(
                            onClick = {
                                onSaveMealOrder(cardStates.map { it.meal })
                                isReordering = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(Res.string.action_save)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                isReordering = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = stringResource(Res.string.action_reorder)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehaviour
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehaviour.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            items(
                items = cardStates,
                key = { state -> state.meal.id }
            ) { cardState ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = cardState.meal.id
                ) { isDragging ->
                    Column {
                        MealSettingsCard(
                            state = cardState,
                            formatTime = formatTime,
                            onSave = {
                                onUpdateMeal(cardState.intoMeal())
                            },
                            shouldShowDeleteDialog = true,
                            onDelete = {
                                onDeleteMeal(cardState.meal)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                            colors = if (isDragging) {
                                MealSettingsCardDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            } else {
                                MealSettingsCardDefaults.colors()
                            },
                            action = if (isReordering) {
                                {
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier
                                            .clearAndSetSemantics { }
                                            .draggableHandle()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DragHandle,
                                            contentDescription = stringResource(
                                                Res.string.action_reorder
                                            )
                                        )
                                    }
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Why is it so wierd?
// Data can't leave inside lazy list item because it will be lost when not visible. It must be
// stored outside the lazy list.
//
// tbh why meals have to be sorted but it works so I won't touch it for now tf
@Composable
fun rememberMealsSettingsCardStates(meals: List<Meal>): MutableState<List<MealSettingsCardState>> {
    val stableMeals = meals.sortedBy { it.id }

    val textFieldStates = stableMeals.map { meal ->
        meal.id to rememberSaveable(
            meal.name,
            stateSaver = TextFieldValue.Saver
        ) {
            mutableStateOf(
                TextFieldValue(
                    text = meal.name,
                    selection = TextRange(meal.name.length)
                )
            )
        }
    }

    val fromTimeStates = stableMeals.map { meal ->
        meal.id to rememberSaveable(
            meal.from,
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.from)
        }
    }

    val toTimeStates = stableMeals.map { meal ->
        meal.id to rememberSaveable(
            meal.to,
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.to)
        }
    }

    val isAllDayStates = stableMeals.map { meal ->
        meal.id to rememberSaveable(meal.isAllDay) {
            mutableStateOf(meal.isAllDay)
        }
    }

    return remember(meals) {
        val res = meals.map { meal ->
            MealSettingsCardState(
                meal = meal,
                nameInput = textFieldStates.first { it.first == meal.id }.second,
                fromTimeInput = fromTimeStates.first { it.first == meal.id }.second,
                toTimeInput = toTimeStates.first { it.first == meal.id }.second,
                isAllDay = isAllDayStates.first { it.first == meal.id }.second
            )
        }

        mutableStateOf(res)
    }
}
