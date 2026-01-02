package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.account.domain.HomeCard
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.extension.hapticDraggableHandle
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HomePersonalizationScreen(
    onBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinInject()
    val savedHomeOrder by viewModel.homeOrder.collectAsStateWithLifecycle()
    val order =
        remember(savedHomeOrder) { savedHomeOrder.mapNotNull { homeCardComposablesMap[it] } }

    HomePersonalizationScreen(
        onBack = onBack,
        order = order,
        onReorder = viewModel::reorder,
        navController = navController,
        modifier = modifier,
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun HomePersonalizationScreen(
    onBack: () -> Unit,
    order: List<HomeCardComposable>,
    onReorder: (List<HomeCard>) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val localOrderState = rememberSaveable(order) { mutableStateOf(order) }
    var localOrder by localOrderState

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

    val moveUp: (HomeCardComposable) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index > 0) {
            localOrder = localOrder.toMutableList().apply { add(index - 1, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val moveDown: (HomeCardComposable) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index < localOrder.size - 1) {
            localOrder = localOrder.toMutableList().apply { add(index + 1, removeAt(index)) }
            true
        } else {
            false
        }
    }

    val latestOnReorder by rememberUpdatedState(onReorder)
    LaunchedEffect(localOrderState) {
        snapshotFlow { localOrderState.value }
            .distinctUntilChanged()
            .filterNot { it.isEmpty() }
            .debounce(50)
            .collectLatest { order -> latestOnReorder(order.map { it.feature }) }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_home_settings)) },
                subtitle = { Text(stringResource(Res.string.description_home_settings)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.padding(8.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues,
        ) {
            items(items = localOrder, key = { it.feature }) { card ->
                val moveUpString = stringResource(Res.string.action_move_up)
                val moveDownString = stringResource(Res.string.action_move_down)

                ReorderableItem(state = reorderableLazyListState, key = card.feature) { isDragging
                    ->
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                    val containerColor by
                        animateColorAsState(
                            if (isDragging) MaterialTheme.colorScheme.surfaceContainerHighest
                            else MaterialTheme.colorScheme.surfaceContainer
                        )

                    card.HomeCardPersonalizationCard(
                        paddingValues = PaddingValues(8.dp),
                        containerColor = containerColor,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shadowElevation = elevation,
                        navController = navController,
                        modifier =
                            Modifier.semantics {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = moveUpString,
                                            action = { moveUp(card) },
                                        ),
                                        CustomAccessibilityAction(
                                            label = moveDownString,
                                            action = { moveDown(card) },
                                        ),
                                    )
                            },
                        dragHandle = { DragHandle(modifier = Modifier.hapticDraggableHandle()) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(48.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = stringResource(Res.string.action_reorder),
        )
    }
}
