package com.maksimowiczm.foodyou.ui.settings.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HomeSettingsScreen(
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    viewModel: HomeSettingsViewModel,
    modifier: Modifier = Modifier
) {
    val order = viewModel.order.collectAsStateWithLifecycle().value

    HomeSettingsScreen(
        order = order,
        onBack = onBack,
        onMealsSettings = onMealsSettings,
        onReorder = viewModel::reorder,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HomeSettingsScreen(
    order: List<HomeCard>,
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    onReorder: (List<HomeCard>) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    var localOrder by remember { mutableStateOf(order) }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localOrder = localOrder.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    val latestOnReorder by rememberUpdatedState(onReorder)
    LaunchedEffect(Unit) {
        snapshotFlow { localOrder }
            .debounce(50)
            .distinctUntilChanged()
            .collectLatest { latestOnReorder(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_home_settings))
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
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .zIndex(1f)
                .testTag(HomeSettingsScreenTestTags.CARDS_LIST),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues
        ) {
            items(
                items = localOrder,
                key = { it.name }
            ) { card ->
                val testTag = HomeSettingsScreenTestTags.Card(card).toString()

                when (card) {
                    HomeCard.Calendar -> CalendarCard(
                        draggableState = reorderableLazyListState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    )

                    HomeCard.Meals -> MealsCard(
                        draggableState = reorderableLazyListState,
                        onMore = onMealsSettings,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    )

                    HomeCard.Calories -> CaloriesCard(
                        draggableState = reorderableLazyListState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MyCard(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun LazyItemScope.CalendarCard(
    draggableState: ReorderableLazyListState,
    modifier: Modifier = Modifier
) {
    ReorderableItem(
        state = draggableState,
        key = HomeCard.Calendar.name,
        modifier = modifier
    ) {
        MyCard {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(Res.string.headline_calendar),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .draggableHandle()
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(Res.string.action_reorder)
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.MealsCard(
    draggableState: ReorderableLazyListState,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    ReorderableItem(
        state = draggableState,
        key = HomeCard.Meals.name,
        modifier = modifier
    ) {
        MyCard {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(Res.string.headline_meals),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onMore
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(Res.string.action_show_more)
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .draggableHandle()
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(Res.string.action_reorder)
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.CaloriesCard(
    draggableState: ReorderableLazyListState,
    modifier: Modifier = Modifier
) {
    ReorderableItem(
        state = draggableState,
        key = HomeCard.Calories.name,
        modifier = modifier
    ) {
        MyCard {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(Res.string.unit_calories),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .draggableHandle()
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(Res.string.action_reorder)
                )
            }
        }
    }
}
