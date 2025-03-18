package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealSettingsCardTestTags
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_reorder
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun MealsSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealsSettingsScreenViewModel = koinViewModel()
) {
    val sortedMeals by viewModel.sortedMeals.collectAsStateWithLifecycle()

    MealsSettingsScreen(
        meals = sortedMeals,
        modifier = modifier
    )
}

@Composable
fun MealsSettingsScreen(meals: List<Meal>, modifier: Modifier = Modifier) {
    val textFieldStates = meals.map { meal ->
        meal.id to rememberTextFieldState(meal.name)
    }

    var internalList by remember {
        mutableStateOf(
            meals.map { meal ->
                Pair<Meal, TextFieldState>(
                    meal,
                    textFieldStates.first { it.first == meal.id }.second
                )
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        internalList = internalList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            items(
                items = internalList,
                key = { (meal, _) -> meal.id }
            ) { (meal, nameInputState) ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = meal.id
                ) { isDragging ->
                    Column {
                        MealCard(
                            nameInputState = nameInputState,
                            meal = meal,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.MealCard(
    nameInputState: TextFieldState,
    meal: Meal,
    modifier: Modifier = Modifier
) {
    val nameInput: @Composable RowScope.() -> Unit = {
        BasicTextField(
            state = nameInputState,
            modifier = Modifier
                .testTag(MealSettingsCardTestTags.NAME_INPUT)
                .defaultMinSize(minWidth = 150.dp)
                .width(IntrinsicSize.Min)
                .weight(1f, false),
            textStyle = LocalTextStyle.current
                .merge(MaterialTheme.typography.headlineMedium)
                .merge(LocalContentColor.current),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            decorator = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f, false)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            it()
                        }

                        HorizontalDivider(
                            color = if (nameInputState.text.isEmpty()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
        )
    }

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            Row {
                nameInput()
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .clearAndSetSemantics { }
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
}
