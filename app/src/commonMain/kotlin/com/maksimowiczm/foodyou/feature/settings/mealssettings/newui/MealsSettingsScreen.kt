package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.LocalTimeInput
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.datetime.LocalTime
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
        formatTime = viewModel::formatTime,
        modifier = modifier
    )
}

@Composable
fun MealsSettingsScreen(
    meals: List<Meal>,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
    val textFieldStates = meals.map { meal ->
        meal.id to rememberTextFieldState(meal.name)
    }

    val fromTimeStates = meals.map { meal ->
        meal.id to rememberSaveable(
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.from)
        }
    }

    val toTimeStates = meals.map { meal ->
        meal.id to rememberSaveable(
            saver = LocalTimeInput.Saver
        ) {
            LocalTimeInput(meal.to)
        }
    }

    var internalList by remember {
        mutableStateOf(
            meals.map { meal ->
                Pair<Meal, Triple<TextFieldState, LocalTimeInput, LocalTimeInput>>(
                    meal,
                    Triple(
                        textFieldStates.first { it.first == meal.id }.second,
                        fromTimeStates.first { it.first == meal.id }.second,
                        toTimeStates.first { it.first == meal.id }.second
                    )
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
            ) { (meal, triple) ->
                val (nameInputState, fromTimeInput, toTimeInput) = triple

                ReorderableItem(
                    state = reorderableLazyListState,
                    key = meal.id
                ) { isDragging ->
                    Column {
                        MealCard(
                            nameInputState = nameInputState,
                            fromTimeInput = fromTimeInput,
                            toTimeInput = toTimeInput,
                            isDirty = false,
                            formatTime = formatTime,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            colors = if (isDragging) {
                                MealCardDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            } else {
                                MealCardDefaults.colors()
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

private enum class ActionButtonState {
    Save,
    Delete
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReorderableCollectionItemScope.MealCard(
    nameInputState: TextFieldState,
    fromTimeInput: LocalTimeInput,
    toTimeInput: LocalTimeInput,
    isDirty: Boolean,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    colors: MealCardColors = MealCardDefaults.colors(),
    shape: Shape = MealCardDefaults.shape
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

    // TODO
    val actionButtonState by remember(Unit) {
        derivedStateOf {
            when {
                nameInputState.text.isEmpty() -> ActionButtonState.Delete
                false -> ActionButtonState.Save
                else -> ActionButtonState.Delete
            }
        }
    }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = { TODO() }
        )
    }

    val actionButton = @Composable {
        when (actionButtonState) {
            ActionButtonState.Save -> FilledIconButton(
                onClick = { TODO() },
                enabled = true,
                modifier = Modifier.testTag(MealSettingsCardTestTags.CONFIRM_BUTTON),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = colors.confirmButtonContainerColor,
                    contentColor = colors.confirmButtonContentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(Res.string.action_save)
                )
            }

            ActionButtonState.Delete -> IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.testTag(MealSettingsCardTestTags.DELETE_BUTTON),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colors.deleteButtonContainerColor,
                    contentColor = colors.deleteButtonContentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.action_delete)
                )
            }
        }
    }

    var showFromTimePicker by rememberSaveable { mutableStateOf(false) }
    var showToTimePicker by rememberSaveable { mutableStateOf(false) }

    val dateInput = @Composable {
        val containerColor by animateColorAsState(
            MaterialTheme.colorScheme.surfaceContainer
        )

        val contentColor by animateColorAsState(
            MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Card(
                onClick = { showFromTimePicker = true },
                modifier = Modifier.testTag(MealSettingsCardTestTags.FROM_TIME_PICKER),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = formatTime(fromTimeInput.value),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = stringResource(Res.string.en_dash),
                style = MaterialTheme.typography.headlineSmall
            )
            Card(
                onClick = { showToTimePicker = true },
                modifier = Modifier.testTag(MealSettingsCardTestTags.TO_TIME_PICKER),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = formatTime(toTimeInput.value),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    if (showFromTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = fromTimeInput.value.hour,
            initialMinute = fromTimeInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showFromTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromTimePicker = false
                        fromTimeInput.onValueChange(
                            LocalTime(
                                hour = timePickerState.hour,
                                minute = timePickerState.minute
                            )
                        )
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.action_confirm)
                    )
                }
            },
            title = {},
            dismissButton = {
                TextButton(
                    onClick = { showFromTimePicker = false }
                ) {
                    Text(
                        text = stringResource(Res.string.action_cancel)
                    )
                }
            }
        ) {
            TimePicker(timePickerState)
        }
    }

    if (showToTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = toTimeInput.value.hour,
            initialMinute = toTimeInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showToTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToTimePicker = false
                        toTimeInput.onValueChange(
                            LocalTime(
                                hour = timePickerState.hour,
                                minute = timePickerState.minute
                            )
                        )
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.action_confirm)
                    )
                }
            },
            title = {},
            dismissButton = {
                TextButton(
                    onClick = { showToTimePicker = false }
                ) {
                    Text(
                        text = stringResource(Res.string.action_cancel)
                    )
                }
            }

        ) {
            TimePicker(timePickerState)
        }
    }

    val surfaceColor by animateColorAsState(
        targetValue = if (false) {
            colors.dirtyContainerColor
        } else {
            colors.containerColor
        }
    )

    val contentColor by animateColorAsState(
        targetValue = if (false) {
            colors.dirtyContentColor
        } else {
            colors.contentColor
        }
    )

    Surface(
        modifier = modifier,
        color = surfaceColor,
        shape = shape
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    nameInput()
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

                Column(
                    modifier = Modifier.testTag(MealSettingsCardTestTags.TIME_PICKER)
                ) {
                    Spacer(Modifier.height(8.dp))

                    dateInput()
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .testTag(MealSettingsCardTestTags.ALL_DAY_SWITCH_CONTAINER)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { TODO() }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = false,
                        onCheckedChange = { TODO() },
                        modifier = Modifier.testTag(MealSettingsCardTestTags.ALL_DAY_SWITCH)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(Res.string.headline_all_day),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = stringResource(Res.string.action_delete)
                )
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = stringResource(Res.string.action_cancel)
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.neutral_permanently_delete_meal)
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.neutral_meal_will_be_deleted_permanently)
            )
        }
    )
}

private data class MealCardColors(
    val containerColor: Color,
    val contentColor: Color,
    val timeContainerColor: Color,
    val timeContentColor: Color,

    val dirtyContainerColor: Color,
    val dirtyContentColor: Color,
    val dirtyTimeContainerColor: Color,
    val dirtyTimeContentColor: Color,

    val confirmButtonContainerColor: Color,
    val confirmButtonContentColor: Color,

    val deleteButtonContainerColor: Color,
    val deleteButtonContentColor: Color
)

private object MealCardDefaults {
    val shape: Shape
        @Composable get() = MaterialTheme.shapes.medium

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        timeContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        timeContentColor: Color = MaterialTheme.colorScheme.onSurface,
        dirtyContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        dirtyContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        dirtyTimeContainerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        dirtyTimeContentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
        confirmButtonContainerColor: Color = MaterialTheme.colorScheme.primary,
        confirmButtonContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        deleteButtonContainerColor: Color = Color.Transparent,
        deleteButtonContentColor: Color = MaterialTheme.colorScheme.onSurface
    ) = MealCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        timeContainerColor = timeContainerColor,
        timeContentColor = timeContentColor,
        dirtyContainerColor = dirtyContainerColor,
        dirtyContentColor = dirtyContentColor,
        dirtyTimeContainerColor = dirtyTimeContainerColor,
        dirtyTimeContentColor = dirtyTimeContentColor,
        confirmButtonContainerColor = confirmButtonContainerColor,
        confirmButtonContentColor = confirmButtonContentColor,
        deleteButtonContainerColor = deleteButtonContainerColor,
        deleteButtonContentColor = deleteButtonContentColor
    )
}

object MealSettingsCardTestTags {
    const val NAME_INPUT = "Name input"
    const val DELETE_BUTTON = "Delete button"
    const val CONFIRM_BUTTON = "Confirm button"
    const val TIME_PICKER = "Time picker"
    const val ALL_DAY_SWITCH_CONTAINER = "All day switch container"
    const val ALL_DAY_SWITCH = "All day switch"
    const val FROM_TIME_PICKER = "From time picker"
    const val TO_TIME_PICKER = "To time picker"
}
