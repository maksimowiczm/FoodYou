package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        modifier = modifier
    )
}

@Composable
fun MealsSettingsScreen(
    meals: List<Meal>,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
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

    var internalList by remember {
        mutableStateOf(
            meals.map { meal ->
                Pair<Meal, Triple<TextFieldState, LocalTimeInput, LocalTimeInput>>(
                    meal,
                    Triple(
                        textFieldStates.first { it.first == meal.id }.second,
                        fromTimeStates.first { it.first == meal.id }.second,
                        toTimeStates.first { it.first == meal.id }.second
                    )
                )
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        internalList = internalList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            items(
                items = internalList,
                key = { (meal, _) -> meal.id }
            ) { (meal, triple) ->
                val (nameInputState, fromTimeInput, toTimeInput) = triple

                ReorderableItem(
                    state = reorderableLazyListState,
                    key = meal.id
                ) { isDragging ->
                    Column {
                        MealsSettingsCard(
                            nameInputState = nameInputState,
                            fromTimeInput = fromTimeInput,
                            toTimeInput = toTimeInput,
                            isDirty = false,
                            formatTime = formatTime,
                            onSave = {},
                            shouldShowDeleteDialog = true,
                            onDelete = {},
                            modifier = Modifier.padding(horizontal = 8.dp),
                            colors = if (isDragging) {
                                MealSettingsCardDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            } else {
                                MealSettingsCardDefaults.colors()
                            },
                            action = {
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
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
