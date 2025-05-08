package com.maksimowiczm.foodyou.ui.settings.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.data.HomePreferences
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HomeSettingsScreen(
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    val order by dataStore
        .observe(HomePreferences.homeOrder)
        .map { it.toHomeCards() }
        .collectAsStateWithLifecycle(dataStore.getBlocking(HomePreferences.homeOrder).toHomeCards())

    HomeSettingsScreen(
        order = order,
        onBack = onBack,
        onMealsSettings = onMealsSettings,
        onReorder = coroutineScope.lambda<List<HomeCard>> {
            dataStore.set(HomePreferences.homeOrder to it.string())
        },
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
        // Must subtract 1 because the first item isn't reorderable
        localOrder = localOrder.toMutableList().apply {
            val fromIndex = from.index - 1
            val toIndex = to.index - 1

            if (fromIndex != toIndex) {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    val latestOnReorder by rememberUpdatedState(onReorder)
    LaunchedEffect(Unit) {
        snapshotFlow { localOrder }
            .debounce(50)
            .distinctUntilChanged()
            .collectLatest { latestOnReorder(it) }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topBar = @Composable {
        LargeTopAppBar(
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
            },
            scrollBehavior = scrollBehavior
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .testTag(HomeSettingsScreenTestTags.CARDS_LIST),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = stringResource(Res.string.description_home_settings),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(
                items = localOrder,
                key = { it.name }
            ) { card ->
                val testTag = HomeSettingsScreenTestTags.Card(card).toString()

                when (card) {
                    HomeCard.Calendar -> MyCard(
                        card = card,
                        draggableState = reorderableLazyListState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    ) {
                        CalendarCardContent(it)
                    }

                    HomeCard.Meals -> MyCard(
                        card = card,
                        draggableState = reorderableLazyListState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    ) {
                        MealsCardContent(it) {
                            onMealsSettings()
                        }
                    }

                    HomeCard.Calories -> MyCard(
                        card = card,
                        draggableState = reorderableLazyListState,
                        modifier = Modifier
                            .testTag(testTag)
                            .padding(horizontal = 8.dp)
                    ) {
                        CaloriesCardContent(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.MyCard(
    card: HomeCard,
    draggableState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
    content: @Composable ReorderableCollectionItemScope.(RowScope) -> Unit
) {
    ReorderableItem(
        state = draggableState,
        key = card.name,
        modifier = modifier
    ) { isDragging ->

        val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
        val containerColor by animateColorAsState(
            if (isDragging) {
                MaterialTheme.colorScheme.surfaceContainerHighest
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        )

        Surface(
            color = containerColor,
            shadowElevation = elevation,
            tonalElevation = elevation,
            shape = CardDefaults.shape
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content(this)
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.CalendarCardContent(rs: RowScope) = with(rs) {
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

@Composable
private fun ReorderableCollectionItemScope.MealsCardContent(rs: RowScope, onMore: () -> Unit) =
    with(rs) {
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

@Composable
private fun ReorderableCollectionItemScope.CaloriesCardContent(rs: RowScope) = with(rs) {
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
