package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import foodyou.app.generated.resources.*
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsScreenViewModel = koinViewModel()
) {
    val meals by viewModel.meals.collectAsStateWithLifecycle()
    val state = rememberMealsSettingsScreenState(meals)

    LaunchedEffect(meals) {
        state.meals = meals
    }

    MealsSettingsScreen(
        onBack = onBack,
        state = state,
        onRankSave = { viewModel.orderMeals(state.meals) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    state: MealsSettingsScreenState,
    onRankSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    val createCardFocusRequester = remember { FocusRequester() }
    LaunchedEffect(state.isCreating) {
        if (state.isCreating) {
            createCardFocusRequester.requestFocus()
        }
    }

    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
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
                title = {
                    Text(
                        text = stringResource(Res.string.headline_meals)
                    )
                },
                actions = {
                    if (state.isReordering) {
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                onRankSave()
                                state.isReordering = false
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
                                    Text(
                                        text = stringResource(Res.string.action_reorder)
                                    )
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(
                                onClick = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                    state.isReordering = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Reorder,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                scrollBehavior = topBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        },
        modifier = modifier
            .imePadding()
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom))
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
                .animateContentSize()
                .fillMaxSize()
        ) {
            ReorderableMeals(
                state = state,
                coroutineScope = coroutineScope
            )

            Column {
                CreateMealSettingsCard(
                    isCreating = state.isCreating,
                    onCreatingChange = { state.isCreating = it },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .focusRequester(createCardFocusRequester),
                    coroutineScope = coroutineScope
                )

                Spacer(Modifier.height(8.dp))

                val bottomPadding = ScaffoldDefaults.contentWindowInsets
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues().calculateBottomPadding()
                Spacer(Modifier.height(bottomPadding))
            }
        }
    }
}

@Composable
private fun ReorderableMeals(state: MealsSettingsScreenState, coroutineScope: CoroutineScope) {
    val hapticFeedback = LocalHapticFeedback.current

    ReorderableColumn(
        modifier = Modifier.fillMaxSize(),
        list = state.meals,
        onSettle = { from, to ->
            val newMeals = state.meals.toMutableList()
            newMeals.add(to, newMeals.removeAt(from))
            state.meals = newMeals
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
        },
        onMove = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    ) { i, meal, isDragging ->
        key(meal.id) {
            val interactionSource = remember { MutableInteractionSource() }

            val containerColor by animateColorAsState(
                if (isDragging) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    MealSettingsCardDefaults.colors().containerColor
                }
            )

            val moveUpString = stringResource(Res.string.action_move_up)
            val moveDownString = stringResource(Res.string.action_move_down)

            Column {
                MealSettingsCard(
                    viewModel = MealSettingsCardViewModel(
                        diaryRepository = koinInject(),
                        stringFormatRepository = koinInject(),
                        mealId = meal.id,
                        coroutineScope = coroutineScope
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .semantics {
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = moveUpString,
                                    action = {
                                        if (i > 0) {
                                            val newMeals = state.meals.toMutableList()
                                            newMeals.add(i - 1, newMeals.removeAt(i))
                                            state.meals = newMeals
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                ),
                                CustomAccessibilityAction(
                                    label = moveDownString,
                                    action = {
                                        if (i < state.meals.size - 1) {
                                            val newMeals = state.meals.toMutableList()
                                            newMeals.add(i + 1, newMeals.removeAt(i))
                                            state.meals = newMeals
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )
                            )
                        },
                    action = if (!state.isReordering) {
                        null
                    } else {
                        {
                            IconButton(
                                modifier = Modifier
                                    .draggableHandle(
                                        interactionSource = interactionSource
                                    )
                                    .clearAndSetSemantics { },
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = stringResource(
                                        Res.string.action_reorder
                                    )
                                )
                            }
                        }
                    },
                    colors = MealSettingsCardDefaults.colors(
                        containerColor = containerColor
                    )
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
