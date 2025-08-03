package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.fooddiary.data.copy
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.hapticDraggableHandle
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun MealSettingsScreen(
    onBack: () -> Unit,
    onMealsCardsSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val mealDao = koinInject<FoodDiaryDatabase>().mealDao
    val meals = mealDao.observeMeals().collectAsStateWithLifecycle(null).value

    val onDelete: (Meal) -> Unit = {
        coroutineScope.launch {
            mealDao.deleteMeal(it)
        }
    }
    val onSave: (mealId: Long, MealCardState) -> Unit = { mealId, state ->
        coroutineScope.launch {
            val meal = mealDao.observeMealById(mealId).first() ?: return@launch

            val (from, to) = if (state.isAllDay) {
                state.fromTime to state.fromTime
            } else {
                state.fromTime to state.toTime
            }

            val updated = meal.copy(
                name = state.name.value,
                from = from,
                to = to
            )

            mealDao.updateMeal(updated)
        }
    }
    val onCreate: (MealCardState) -> Unit = { state ->
        coroutineScope.launch {
            val (from, to) = if (state.isAllDay) {
                state.fromTime to state.fromTime
            } else {
                state.fromTime to state.toTime
            }

            val meal = Meal(
                name = state.name.value,
                fromHour = from.hour,
                fromMinute = from.minute,
                toHour = to.hour,
                toMinute = to.minute
            )

            mealDao.insertWithLastRank(meal)
        }
    }
    val onSaveOrder: (List<Meal>) -> Unit = { meals ->
        coroutineScope.launch {
            val map = meals
                .mapIndexed { i, meal -> meal.id to i }
                .toMap()

            mealDao.updateMealsRanks(map)
        }
    }

    if (meals == null) {
        // TODO loading state
    } else {
        MealSettingsScreen(
            onBack = onBack,
            onMealsCardsSettings = onMealsCardsSettings,
            onDelete = onDelete,
            onSave = onSave,
            onCreate = onCreate,
            onSaveOrder = onSaveOrder,
            meals = meals,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MealSettingsScreen(
    onBack: () -> Unit,
    onMealsCardsSettings: () -> Unit,
    onDelete: (Meal) -> Unit,
    onSave: (mealId: Long, MealCardState) -> Unit,
    onCreate: (MealCardState) -> Unit,
    onSaveOrder: (List<Meal>) -> Unit,
    meals: List<Meal>,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    var isReordering by rememberSaveable { mutableStateOf(false) }
    var showForm by rememberSaveable { mutableStateOf(false) }
    val formFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showForm) {
        runCatching {
            formFocusRequester.requestFocus()
        }
    }

    val lazyListState = rememberLazyListState()
    var mealsOrder by remember(meals) { mutableStateOf(meals) }
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        mealsOrder = mealsOrder.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_meals))
                },
                navigationIcon = {
                    ArrowBackIconButton(onBack)
                },
                actions = {
                    if (isReordering) {
                        IconButton(
                            onClick = {
                                onSaveOrder(mealsOrder)
                                isReordering = false
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(Res.string.action_save)
                            )
                        }
                    } else {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(Res.string.action_reorder))
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = {
                                    isReordering = true
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Reorder,
                                    contentDescription = stringResource(Res.string.action_reorder)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues.add(vertical = 8.dp)
        ) {
            items(
                items = mealsOrder,
                key = { it.id }
            ) { meal ->
                val state = rememberMealCardState(meal)

                ReorderableItem(
                    state = reorderableLazyListState,
                    key = meal.id
                ) { isDragging ->
                    MealCard(
                        state = state,
                        isDragging = isDragging,
                        onSave = { onSave(meal.id, state) },
                        onDelete = { onDelete(meal) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        action = if (isReordering) {
                            {
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier
                                        .clearAndSetSemantics { }
                                        .hapticDraggableHandle()
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
                }
            }

            item {
                CreateMealCard(
                    showForm = showForm,
                    onShowFormChange = { showForm = it },
                    onSave = onCreate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .focusRequester(formFocusRequester)
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.headline_meals_cards_settings))
                    },
                    modifier = Modifier.clickable { onMealsCardsSettings() }
                )
            }
        }
    }
}

@Composable
private fun CreateMealCard(
    showForm: Boolean,
    onShowFormChange: (Boolean) -> Unit,
    onSave: (MealCardState) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showForm) {
        val state = rememberMealCardState(null)

        MealCard(
            state = state,
            isDragging = false,
            onSave = {
                onSave(state)
                onShowFormChange(false)
            },
            onDelete = { onShowFormChange(false) },
            modifier = modifier,
            action = {
                when {
                    !state.isModified -> IconButton(
                        onClick = { onShowFormChange(false) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }

                    else -> FilledIconButton(
                        onClick = {
                            onSave(state)
                            onShowFormChange(false)
                        },
                        enabled = state.isValid
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(Res.string.action_save)
                        )
                    }
                }
            }
        )
    } else {
        Surface(
            onClick = { onShowFormChange(true) },
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.action_add_meal)
                )
            }
        }
    }
}
