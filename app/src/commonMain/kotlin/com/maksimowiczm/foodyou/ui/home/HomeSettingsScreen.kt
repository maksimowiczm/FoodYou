package com.maksimowiczm.foodyou.ui.home

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
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.preferences.HomeCard
import com.maksimowiczm.foodyou.preferences.HomeOrder
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.hapticDraggableHandle
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HomeSettingsScreen(
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    modifier: Modifier = Modifier,
    homeOrder: HomeOrder = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()

    val order by homeOrder.observe()
        .collectAsStateWithLifecycle(homeOrder.getBlocking())

    HomeSettingsScreen(
        order = order,
        onBack = onBack,
        onMealsSettings = onMealsSettings,
        onGoalsSettings = onGoalsSettings,
        onReorder = coroutineScope.lambda<List<HomeCard>> {
            homeOrder.set(it)
        },
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    FlowPreview::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HomeSettingsScreen(
    order: List<HomeCard>,
    onBack: () -> Unit,
    onMealsSettings: () -> Unit,
    onGoalsSettings: () -> Unit,
    onReorder: (List<HomeCard>) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    var localOrder by rememberSaveable { mutableStateOf(order) }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localOrder = localOrder.toMutableList().apply {
            val fromIndex = from.index
            val toIndex = to.index

            if (fromIndex != toIndex) {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    val moveUp: (HomeCard) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index > 0) {
            localOrder = localOrder.toMutableList().apply {
                add(index - 1, removeAt(index))
            }
            true
        } else {
            false
        }
    }

    val moveDown: (HomeCard) -> Boolean = {
        val index = localOrder.indexOf(it)
        if (index < localOrder.size - 1) {
            localOrder = localOrder.toMutableList().apply {
                add(index + 1, removeAt(index))
            }
            true
        } else {
            false
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

    Scaffold(
        modifier = modifier,
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_home_settings)) },
                subtitle = { Text(stringResource(Res.string.description_home_settings)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
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
                MyCard(
                    card = card,
                    draggableState = reorderableLazyListState,
                    moveUp = { moveUp(card) },
                    moveDown = { moveDown(card) },
                    modifier = Modifier.testTag(testTag)
                ) {
                    when (card) {
                        HomeCard.Calendar -> CalendarCardContent(it)
                        HomeCard.Meals -> MealsCardContent(
                            rs = it,
                            onMore = onMealsSettings
                        )

                        HomeCard.Goals -> GoalsCardContent(
                            rs = it,
                            onMore = onGoalsSettings
                        )
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
    moveUp: () -> Boolean,
    moveDown: () -> Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ReorderableCollectionItemScope.(RowScope) -> Unit
) {
    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

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
            modifier = Modifier.semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = moveUpString,
                        action = moveUp
                    ),
                    CustomAccessibilityAction(
                        label = moveDownString,
                        action = moveDown
                    )
                )
            },
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
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null
        )
    }
    Spacer(Modifier.width(16.dp))
    Text(stringResource(Res.string.headline_calendar))
    Spacer(Modifier.weight(1f))
    DragHandle(
        modifier = Modifier.hapticDraggableHandle(this@CalendarCardContent)
    )
}

@Composable
private fun ReorderableCollectionItemScope.MealsCardContent(rs: RowScope, onMore: () -> Unit) =
    with(rs) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(stringResource(Res.string.headline_meals))
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onMore
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.action_show_more)
            )
        }
        DragHandle(
            modifier = Modifier.hapticDraggableHandle(this@MealsCardContent)
        )
    }

@Composable
private fun ReorderableCollectionItemScope.GoalsCardContent(rs: RowScope, onMore: () -> Unit) =
    with(rs) {
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
        Text(stringResource(Res.string.headline_daily_goals))
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onMore
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.action_show_more)
            )
        }
        DragHandle(
            modifier = Modifier.hapticDraggableHandle(this@GoalsCardContent)
        )
    }

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    IconButton(
        onClick = {},
        modifier = Modifier
            .clearAndSetSemantics {}
            .then(modifier)
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = stringResource(Res.string.action_reorder)
        )
    }
}
