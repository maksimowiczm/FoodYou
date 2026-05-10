package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.ResetToDefaultDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.hapticDraggableHandle
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PersonalizeNutritionFactsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: PersonalizeNutritionFactsViewModel = koinViewModel()
    val order by viewModel.order.collectAsStateWithLifecycle()

    PersonalizeNutritionFactsScreen(
        order = order,
        onUpdateOrder = viewModel::updateOrder,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun PersonalizeNutritionFactsScreen(
    order: List<NutrientsOrder>,
    onUpdateOrder: (List<NutrientsOrder>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val lazyListState = rememberLazyListState()
    val localOrder = rememberSaveable(order) { mutableStateOf(order) }

    val latestOnReorder by rememberUpdatedState(onUpdateOrder)
    LaunchedEffect(localOrder) {
        snapshotFlow { localOrder.value }
            .drop(1)
            .debounce(50)
            .distinctUntilChanged()
            .collectLatest { latestOnReorder(it) }
    }

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            localOrder.value =
                localOrder.value.toMutableList().apply {
                    val fromIndex = from.index
                    val toIndex = to.index

                    if (fromIndex != toIndex) add(toIndex, removeAt(fromIndex))
                }
        }

    val moveUp: (NutrientsOrder) -> Boolean = {
        val index = localOrder.value.indexOf(it)
        if (index > 0) {
            localOrder.value =
                localOrder.value.toMutableList().apply { add(index, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val moveDown: (NutrientsOrder) -> Boolean = {
        val index = localOrder.value.indexOf(it)
        if (index < localOrder.value.size - 1) {
            localOrder.value =
                localOrder.value.toMutableList().apply { add(index, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    if (showResetDialog) {
        ResetToDefaultDialog(
            onDismissRequest = { showResetDialog = false },
            onConfirm = {
                onUpdateOrder(NutrientsOrder.defaultOrder)
                showResetDialog = false
            },
        ) {
            Text(stringResource(Res.string.description_reset_nutrition_facts))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_nutrition_facts)) },
                subtitle = {
                    Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_reset_settings),
                            contentDescription =
                                stringResource(Res.string.headline_reset_to_default),
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
            itemsIndexed(items = localOrder.value, key = { _, it -> it.name }) { index, item ->
                ReorderableItem(state = reorderableLazyListState, key = item.name) { isDragging ->
                    val containerColor by
                        animateColorAsState(
                            if (isDragging) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer
                        )

                    val elevation =
                        ListItemDefaults.elevation(
                            elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp).value
                        )
                    val colors =
                        ListItemDefaults.segmentedColors(
                            containerColor = containerColor,
                            contentColor = contentColorFor(containerColor),
                        )

                    SegmentedListItem(
                        shapes = ListItemDefaults.segmentedShapes(index, localOrder.value.size),
                        modifier =
                            Modifier.fillMaxWidth().semantics {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = moveUpString,
                                            action = { moveUp(item) },
                                        ),
                                        CustomAccessibilityAction(
                                            label = moveDownString,
                                            action = { moveDown(item) },
                                        ),
                                    )
                            },
                        onClick = {},
                        trailingContent = { DragHandle(Modifier.hapticDraggableHandle()) },
                        elevation = elevation,
                        colors = colors,
                        content = { Text(item.stringResource()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Box(modifier.clearAndSetSemantics {}) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = null,
            modifier = Modifier.clickable(onClick = {}, indication = null, interactionSource = null),
        )
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
            onUpdateOrder = {},
            onBack = {},
        )
    }
}
