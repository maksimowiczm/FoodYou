package com.maksimowiczm.foodyou.features.personalization

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PersonalizeNutritionFactsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: PersonalizeNutritionFactsViewModel = koinViewModel()
    val order by viewModel.order.collectAsStateWithLifecycle()

    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            PersonalizeNutritionFactsEvent.Updated -> onBack()
        }
    }

    PersonalizeNutritionFactsScreen(
        order = order,
        onSaveOrder = { newOrder ->
            if (newOrder == order) onBack() else viewModel.updateOrder(newOrder)
        },
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun PersonalizeNutritionFactsScreen(
    order: List<NutrientsOrder>,
    onSaveOrder: (List<NutrientsOrder>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val lazyListState = rememberLazyListState()
    val localOrder =
        rememberSaveable(
            order,
            saver =
                Saver(
                    save = { state -> state.map { it.ordinal } },
                    restore = { saved ->
                        saved.map { NutrientsOrder.entries[it] }.toMutableStateList()
                    },
                ),
        ) {
            order.toMutableStateList()
        }

    val isModified by remember(order) { derivedStateOf { localOrder.toList() != order } }
    val isDefaultOrder by remember {
        derivedStateOf { localOrder.toList() == NutrientsOrder.defaultOrder }
    }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isModified,
        onBackCompleted = { showDiscardDialog = true },
    )

    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack)
    }

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            val fromIndex = from.index
            val toIndex = to.index

            if (fromIndex != toIndex) localOrder.add(toIndex, localOrder.removeAt(fromIndex))
        }

    val moveUp: (NutrientsOrder) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index > 0) {
            localOrder.add(index - 1, localOrder.removeAt(index))
            true
        } else {
            false
        }
    }

    val moveDown: (NutrientsOrder) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index < localOrder.size - 1) {
            localOrder.add(index + 1, localOrder.removeAt(index))
            true
        } else {
            false
        }
    }

    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.semantics(mergeDescendants = true) {},
                title = {
                    Text(
                        text = stringResource(Res.string.headline_nutrition_facts),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                subtitle = {
                    Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
                },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = { if (isModified) showDiscardDialog = true else onBack() }
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                actions = {
                    if (isModified) {
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    positioning = TooltipAnchorPosition.Below
                                ),
                            tooltip = {
                                PlainTooltip { Text(stringResource(Res.string.action_undo)) }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = {
                                    localOrder.clear()
                                    localOrder.addAll(order)
                                },
                                shapes = IconButtonDefaults.shapes(),
                                modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
                                enabled = isModified,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Undo,
                                    contentDescription = stringResource(Res.string.action_undo),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                    } else if (!isDefaultOrder) {
                        TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    positioning = TooltipAnchorPosition.Below
                                ),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(Res.string.headline_reset_to_default))
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = {
                                    localOrder.clear()
                                    localOrder.addAll(NutrientsOrder.defaultOrder)
                                },
                                shapes = IconButtonDefaults.shapes(),
                                modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription =
                                        stringResource(Res.string.headline_reset_to_default),
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                )
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    FilledIconButton(
                        onClick = { onSaveOrder(localOrder.toList()) },
                        shapes = IconButtonDefaults.shapes(),
                        modifier =
                            Modifier.size(
                                IconButtonDefaults.smallContainerSize(
                                    IconButtonDefaults.IconButtonWidthOption.Wide
                                )
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.action_save),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues.add(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            itemsIndexed(items = localOrder, key = { _, it -> it.name }) { index, item ->
                val interactionSource = remember { MutableInteractionSource() }
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = item.name,
                    animateItemModifier =
                        Modifier.animateItem(
                            placementSpec = MaterialTheme.motionScheme.fastSpatialSpec()
                        ),
                ) { isDragging ->
                    val largeShape = MaterialTheme.shapes.large
                    val extraSmallShape = MaterialTheme.shapes.extraSmall
                    val shapes =
                        remember(index, localOrder.size, largeShape, extraSmallShape) {
                            val base =
                                if (localOrder.size == 1) largeShape
                                else if (index == 0)
                                    extraSmallShape.copy(
                                        topStart = largeShape.topStart,
                                        topEnd = largeShape.topEnd,
                                    )
                                else if (index == localOrder.size - 1)
                                    extraSmallShape.copy(
                                        bottomStart = largeShape.topStart,
                                        bottomEnd = largeShape.topEnd,
                                    )
                                else extraSmallShape

                            InteractionShapes(
                                shape = base,
                                pressedShape = largeShape,
                                draggedShape = largeShape,
                            )
                        }
                    val animatedShape = rememberInteractionAnimatedShape(shapes, interactionSource)

                    val itemName = item.stringResource()

                    val containerColor by
                        animateColorAsState(
                            if (isDragging) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer,
                            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                        )
                    val color =
                        if (isDragging) MaterialTheme.colorScheme.onTertiaryContainer
                        else MaterialTheme.colorScheme.onSurface

                    val elevation by
                        animateDpAsState(
                            if (isDragging) 8.dp else 0.dp,
                            animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                        )

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth()
                                .semantics {
                                    stateDescription = "${index + 1} of ${localOrder.size}"
                                    customActions = buildList {
                                        if (index > 0) {
                                            add(
                                                CustomAccessibilityAction(
                                                    label = "$moveUpString $itemName",
                                                    action = { moveUp(item) },
                                                )
                                            )
                                        }
                                        if (index < localOrder.size - 1) {
                                            add(
                                                CustomAccessibilityAction(
                                                    label = "$moveDownString $itemName",
                                                    action = { moveDown(item) },
                                                )
                                            )
                                        }
                                    }
                                }
                                .draggableHandle(
                                    interactionSource = interactionSource,
                                    onDragStarted = {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.GestureThresholdActivate
                                        )
                                    },
                                    onDragStopped = {
                                        hapticFeedback.performHapticFeedback(
                                            HapticFeedbackType.GestureEnd
                                        )
                                    },
                                ),
                        shape = animatedShape,
                        color = containerColor,
                        contentColor = color,
                        shadowElevation = elevation,
                    ) {
                        Row(
                            modifier =
                                Modifier.padding(ListItemDefaults.ContentPadding.horizontal()),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.stringResource(),
                                modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                            )
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutrientsOrder.stringResource(): String =
    when (this) {
        NutrientsOrder.Proteins -> stringResource(Res.string.nutriment_proteins)
        NutrientsOrder.Fats -> stringResource(Res.string.nutriment_fats)
        NutrientsOrder.Carbohydrates -> stringResource(Res.string.nutriment_carbohydrates)
        NutrientsOrder.Other -> stringResource(Res.string.headline_other)
        NutrientsOrder.Vitamins -> stringResource(Res.string.headline_vitamins)
        NutrientsOrder.Minerals -> stringResource(Res.string.headline_minerals)
    }

@Preview
@Composable
private fun PersonalizeNutritionFactsScreenPreview() {
    PreviewFoodYouTheme {
        PersonalizeNutritionFactsScreen(
            order = NutrientsOrder.defaultOrder,
            onSaveOrder = {},
            onBack = {},
        )
    }
}
