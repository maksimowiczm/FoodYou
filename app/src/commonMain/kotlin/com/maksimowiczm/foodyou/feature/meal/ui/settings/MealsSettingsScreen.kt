package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.ext.performToggle
import com.maksimowiczm.foodyou.feature.meal.domain.Meal
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsScreenViewModel = koinViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val useTimeBasedSorting by viewModel.useTimeBasedSorting.collectAsStateWithLifecycle()
    val includeAllDayMeals by viewModel.includeAllDayMeals.collectAsStateWithLifecycle()

    // TODO shimmer?
    when (val meals = meals) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> MealsSettingsScreen(
            meals = meals,
            useTimeBasedSorting = useTimeBasedSorting,
            includeAllDayMeals = includeAllDayMeals,
            onBack = onBack,
            formatTime = remember(viewModel) { viewModel::formatTime },
            onCreateMeal = remember(viewModel) { viewModel::createMeal },
            onUpdateMeal = remember(viewModel) { viewModel::updateMeal },
            onDeleteMeal = remember(viewModel) { viewModel::deleteMeal },
            onSaveMealOrder = remember(viewModel) { viewModel::updateMealsRanks },
            onToggleTimeBasedSorting = remember(viewModel) { viewModel::toggleTimeBasedSorting },
            onToggleIncludeAllDayMeals = remember(viewModel) {
                viewModel::toggleIncludeAllDayMeals
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealsSettingsScreen(
    meals: List<Meal>,
    useTimeBasedSorting: Boolean,
    includeAllDayMeals: Boolean,
    onBack: () -> Unit,
    formatTime: (LocalTime) -> String,
    onCreateMeal: (String, LocalTime, LocalTime) -> Unit,
    onUpdateMeal: (Meal) -> Unit,
    onDeleteMeal: (Meal) -> Unit,
    onSaveMealOrder: (List<Meal>) -> Unit,
    onToggleTimeBasedSorting: (Boolean) -> Unit,
    onToggleIncludeAllDayMeals: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hapticFeedback: HapticFeedback = LocalHapticFeedback.current
) {
    var cardStates by rememberMealsSettingsCardStates(meals)

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Must subtract 1 because the first item isn't reorderable
        cardStates = cardStates.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    LaunchedEffect(reorderableLazyListState) {
        var previousState: Boolean? = null
        snapshotFlow { reorderableLazyListState.isAnyItemDragging }.collect {
            val type = when {
                previousState == false && it -> HapticFeedbackType.GestureThresholdActivate
                previousState == true && !it -> HapticFeedbackType.GestureEnd
                else -> null
            }

            if (type != null) {
                hapticFeedback.performHapticFeedback(type)
            }

            previousState = it
        }
    }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    var isReordering by rememberSaveable { mutableStateOf(false) }
    var isCreating by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isCreating) {
        runCatching {
            focusRequester.requestFocus()
        }
    }

    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

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
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                scrollBehavior = scrollBehaviour
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehaviour.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = cardStates,
                key = { state -> state.meal.id }
            ) { cardState ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = cardState.meal.id
                ) { isDragging ->

                    val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    val shadowElevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

                    MealCard(
                        state = cardState,
                        formatTime = formatTime,
                        onSave = {
                            onUpdateMeal(cardState.intoMeal())
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                        },
                        shouldShowDeleteDialog = true, onDelete = {
                            onDeleteMeal(cardState.meal)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp).semantics {
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = moveUpString,
                                    action = {
                                        val index = cardStates.indexOf(cardState)

                                        if (index > 0) {
                                            cardStates = cardStates.toMutableList().apply {
                                                add(index - 1, removeAt(index))
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                                CustomAccessibilityAction(
                                    label = moveDownString,
                                    action = {
                                        val index = cardStates.indexOf(cardState)

                                        if (index < cardStates.size - 1) {
                                            cardStates = cardStates.toMutableList().apply {
                                                add(index + 1, removeAt(index))
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )
                            )
                        },
                        colors = if (isDragging) {
                            MealCardDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        } else {
                            MealCardDefaults.colors()
                        },
                        tonalElevation = tonalElevation,
                        shadowElevation = shadowElevation,
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
                }
            }

            item {
                CreateMealCard(
                    isCreating = isCreating,
                    onCreatingChange = { isCreating = it },
                    onCreate = onCreateMeal,
                    formatTime = formatTime,
                    modifier = Modifier.padding(horizontal = 8.dp).focusRequester(focusRequester)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_time_based_ordering),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                val toggleTimeBased = { newState: Boolean ->
                    onToggleTimeBasedSorting(newState)
                    hapticFeedback.performToggle(newState)
                }

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.action_use_time_based_ordering)
                        )
                    },
                    modifier = Modifier.clickable { toggleTimeBased(!useTimeBasedSorting) },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                Res.string.description_time_based_meals_sorting
                            )
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = useTimeBasedSorting,
                            onCheckedChange = toggleTimeBased
                        )
                    }
                )

                val toggleIncludeAllDayMeals = { newState: Boolean ->
                    onToggleIncludeAllDayMeals(newState)
                    hapticFeedback.performToggle(newState)
                }

                val contentColor = if (useTimeBasedSorting) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.outline
                }
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.action_include_all_day_meals)
                        )
                    },
                    modifier = Modifier.clickable(
                        enabled = useTimeBasedSorting
                    ) {
                        toggleIncludeAllDayMeals(!includeAllDayMeals)
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(
                                Res.string.description_action_include_all_day_meals
                            )
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = includeAllDayMeals,
                            onCheckedChange = toggleIncludeAllDayMeals,
                            enabled = useTimeBasedSorting
                        )
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = contentColor,
                        supportingColor = contentColor
                    )
                )
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
private fun rememberMealsSettingsCardStates(
    meals: List<Meal>
): MutableState<List<MealCardStateWithMeal>> {
    val stableMeals = meals.sortedBy { it.id }

    val textFieldStates = stableMeals.associate { meal ->
        meal.id to key(meal.id) { rememberTextFieldState(meal.name) }
    }

    val fromTimeStates = stableMeals.associate { meal ->
        meal.id to rememberSaveable(
            meal.from,
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.from)
        }
    }

    val toTimeStates = stableMeals.associate { meal ->
        meal.id to rememberSaveable(
            meal.to,
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.to)
        }
    }

    val isAllDayStates = stableMeals.associate { meal ->
        meal.id to rememberSaveable(meal.isAllDay) {
            mutableStateOf(meal.isAllDay)
        }
    }

    return remember(meals) {
        val state = meals.map { meal ->
            val id = meal.id

            MealCardStateWithMeal(
                meal = meal,
                nameInput = textFieldStates[id]!!,
                fromTimeInput = fromTimeStates[id]!!,
                toTimeInput = toTimeStates[id]!!,
                isAllDay = isAllDayStates[id]!!
            )
        }

        mutableStateOf(state)
    }
}
