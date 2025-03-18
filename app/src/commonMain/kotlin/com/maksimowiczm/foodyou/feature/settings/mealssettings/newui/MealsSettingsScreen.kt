package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
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
                                onSaveMealOrder(cardStates.map { it.first })
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
            modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            items(
                items = cardStates,
                key = { (meal, _) -> meal.id }
            ) { (meal, cardState) ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = meal.id
                ) { isDragging ->
                    Column {
                        MealsSettingsCard(
                            state = cardState,
                            isDirty = false,
                            formatTime = formatTime,
                            onSave = {
                                onUpdateMeal(
                                    meal.copy(
                                        name = cardState.nameInput.text.toString(),
                                        from = cardState.fromTimeInput.value,
                                        to = cardState.toTimeInput.value
                                    )
                                )
                            },
                            shouldShowDeleteDialog = true,
                            onDelete = {
                                onDeleteMeal(meal)
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

@Composable
fun rememberMealsSettingsCardStates(
    meals: List<Meal>
): MutableState<List<Pair<Meal, MealsSettingsCardState>>> {
    val textFieldStates = meals.map { meal ->
        meal.id to rememberTextFieldState(meal.name)
    }

    val fromTimeStates = meals.map { meal ->
        meal.id to rememberSaveable(
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.from)
        }
    }

    val toTimeStates = meals.map { meal ->
        meal.id to rememberSaveable(
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.to)
        }
    }

    val isAllDayStates = meals.map { meal ->
        meal.id to rememberSaveable { mutableStateOf(false) }
    }

    return remember(meals) {
        mutableStateOf(
            meals.map { meal ->
                Pair<Meal, MealsSettingsCardState>(
                    meal,
                    MealsSettingsCardState(
                        textFieldStates.first { it.first == meal.id }.second,
                        fromTimeStates.first { it.first == meal.id }.second,
                        toTimeStates.first { it.first == meal.id }.second,
                        isAllDayStates.first { it.first == meal.id }.second
                    )
                )
            }
        )
    }
}
