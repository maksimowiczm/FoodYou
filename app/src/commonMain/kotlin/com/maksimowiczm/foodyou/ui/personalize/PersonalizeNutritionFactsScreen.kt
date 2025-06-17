package com.maksimowiczm.foodyou.ui.personalize

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.model.NutritionFactsField
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.component.ResetToDefaultDialog
import com.maksimowiczm.foodyou.core.ui.ext.add
import com.maksimowiczm.foodyou.core.ui.nutrition.NutritionFactsListPreference
import com.maksimowiczm.foodyou.core.ui.nutrition.NutritionFactsListPreferences
import com.maksimowiczm.foodyou.core.ui.res.stringResource
import foodyou.app.generated.resources.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.hapticDraggableHandle
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PersonalizeNutritionFactsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    preference: NutritionFactsListPreference = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()

    val (order, enabled) = preference.collectAsStateWithLifecycle(preference.getBlocking()).value

    PersonalizeNutritionFactsScreen(
        order = order,
        enabledNutritionFacts = enabled,
        onToggle = coroutineScope.lambda<NutritionFactsField> {
            val newEnabledNutritionFacts = if (it in enabled) {
                enabled - it
            } else {
                enabled + it
            }

            val preferences = NutritionFactsListPreferences(
                order = order,
                enabled = newEnabledNutritionFacts
            )

            preference.set(preferences)
        },
        onUpdateOrder = coroutineScope.lambda<List<NutritionFactsField>> {
            val preferences = NutritionFactsListPreferences(
                order = it,
                enabled = enabled
            )

            preference.set(preferences)
        },
        onReset = coroutineScope.lambda {
            preference.reset()
        },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)
@Composable
private fun PersonalizeNutritionFactsScreen(
    order: List<NutritionFactsField>,
    enabledNutritionFacts: List<NutritionFactsField>,
    onToggle: (NutritionFactsField) -> Unit,
    onUpdateOrder: (List<NutritionFactsField>) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    if (showResetDialog) {
        ResetToDefaultDialog(
            onDismissRequest = { showResetDialog = false },
            onConfirm = {
                onReset()
                showResetDialog = false
            },
            text = {
                Text(stringResource(Res.string.description_reset_nutrition_facts))
            }
        )
    }

    val lazyListState = rememberLazyListState()
    var localOrder by rememberSaveable { mutableStateOf(order) }

    LaunchedEffect(order) {
        if (order != localOrder) {
            localOrder = order
        }
    }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localOrder = localOrder.toMutableList().apply {
            val fromIndex = from.index - 1
            val toIndex = to.index - 1

            if (fromIndex != toIndex) {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    val moveUp: (NutritionFactsField) -> Boolean = {
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

    val moveDown: (NutritionFactsField) -> Boolean = {
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

    val latestOnReorder by rememberUpdatedState(onUpdateOrder)
    LaunchedEffect(Unit) {
        snapshotFlow { localOrder }
            .debounce(50)
            .distinctUntilChanged()
            .collectLatest { latestOnReorder(it) }
    }

    val moveUpString = stringResource(Res.string.action_move_up)
    val moveDownString = stringResource(Res.string.action_move_down)

    val showWarning = remember(enabledNutritionFacts) {
        !enabledNutritionFacts.contains(NutritionFactsField.Energy) ||
            !enabledNutritionFacts.contains(NutritionFactsField.Proteins) ||
            !enabledNutritionFacts.contains(NutritionFactsField.Carbohydrates) ||
            !enabledNutritionFacts.contains(NutritionFactsField.Fats)
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.headline_nutrition_facts)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_reset_settings),
                            contentDescription = stringResource(Res.string.action_reset_ordering)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues.add(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                ) {
                    AnimatedVisibility(
                        visible = showWarning,
                        enter = expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()),
                        exit = shrinkVertically(MaterialTheme.motionScheme.defaultSpatialSpec())
                    ) {
                        Column {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(
                                            Res.string.description_warning_nutrition_facts_1
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = stringResource(
                                            Res.string.description_warning_nutrition_facts_2
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Text(
                        text = stringResource(Res.string.description_personalize_nutrition_facts),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            Res.string.description_personalize_nutrition_facts_help
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(
                            Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .size(16.dp)
                        ) {
                            drawRect(colorScheme.primaryContainer)
                        }
                        Text(
                            text = stringResource(Res.string.headline_enabled),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(
                            Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .size(16.dp)
                        ) {
                            drawRect(colorScheme.surfaceContainer)
                        }
                        Text(
                            text = stringResource(Res.string.headline_disabled),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            items(
                items = localOrder,
                key = { it.name }
            ) {
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = it.name
                ) { isDragging ->
                    ListItem(
                        item = it,
                        isDragging = isDragging,
                        isEnabled = it in enabledNutritionFacts,
                        onClick = { onToggle(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .semantics {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = moveUpString,
                                        action = { moveUp(it) }
                                    ),
                                    CustomAccessibilityAction(
                                        label = moveDownString,
                                        action = { moveDown(it) }
                                    )
                                )
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.ListItem(
    item: NutritionFactsField,
    isDragging: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isEnabled) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }
    )
    val contentColor by animateColorAsState(
        targetValue = if (isEnabled) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.outline
        }
    )
    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color,
        contentColor = contentColor,
        shadowElevation = elevation,
        tonalElevation = elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.stringResource(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1f))
            DragHandle(Modifier.hapticDraggableHandle(this@ListItem))
        }
    }
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Box(modifier.clearAndSetSemantics {}) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = null,
            modifier = Modifier.clickable(
                onClick = {},
                indication = null,
                interactionSource = null
            )
        )
    }
}
