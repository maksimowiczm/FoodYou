package com.maksimowiczm.foodyou.feature.settings.personalization.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.settings.NutrientsOrder
import com.maksimowiczm.foodyou.feature.settings.personalization.presentation.PersonalizeNutritionFactsViewModel
import com.maksimowiczm.foodyou.shared.ui.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.ResetToDefaultDialog
import com.maksimowiczm.foodyou.shared.ui.ext.add
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.hapticDraggableHandle
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PersonalizeNutritionFactsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: PersonalizeNutritionFactsViewModel = koinViewModel()
    val order by viewModel.order.collectAsStateWithLifecycle()

    PersonalizeNutritionFactsScreen(
        order = order,
        onUpdateOrder = viewModel::updateOrder,
        onReset = viewModel::resetOrder,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class,
)
@Composable
private fun PersonalizeNutritionFactsScreen(
    order: List<NutrientsOrder>,
    onUpdateOrder: (List<NutrientsOrder>) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    if (showResetDialog) {
        ResetToDefaultDialog(
            onDismissRequest = { showResetDialog = false },
            onConfirm = {
                onReset()
                showResetDialog = false
            },
        ) {
            Text(stringResource(Res.string.description_reset_nutrition_facts))
        }
    }

    val lazyListState = rememberLazyListState()
    var localOrder by rememberSaveable(order) { mutableStateOf(order) }

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            localOrder =
                localOrder.toMutableList().apply {
                    val fromIndex = from.index
                    val toIndex = to.index

                    if (fromIndex != toIndex) {
                        add(toIndex, removeAt(fromIndex))
                    }
                }
        }

    val moveUp: (NutrientsOrder) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index > 0) {
            localOrder = localOrder.toMutableList().apply { add(index, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val moveDown: (NutrientsOrder) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index < localOrder.size - 1) {
            localOrder = localOrder.toMutableList().apply { add(index, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val latestOnReorder by rememberUpdatedState(onUpdateOrder)
    LaunchedEffect(Unit) {
        snapshotFlow { localOrder }
            .debounce(50)
            .distinctUntilChanged()
            .collectLatest { latestOnReorder(it) }
    }

    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_nutrition_facts)) },
                subtitle = {
                    Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_reset_settings),
                            contentDescription = stringResource(Res.string.action_reset_ordering),
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
            contentPadding = paddingValues.add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = localOrder, key = { it.name }) {
                ReorderableItem(state = reorderableLazyListState, key = it.name) { isDragging ->
                    ListItem(
                        item = it,
                        isDragging = isDragging,
                        modifier =
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp).semantics {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = moveUpString,
                                            action = { moveUp(it) },
                                        ),
                                        CustomAccessibilityAction(
                                            label = moveDownString,
                                            action = { moveDown(it) },
                                        ),
                                    )
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.ListItem(
    item: NutrientsOrder,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
) {
    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = elevation,
        tonalElevation = elevation,
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = item.stringResource(), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f))
            DragHandle(Modifier.hapticDraggableHandle())
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
