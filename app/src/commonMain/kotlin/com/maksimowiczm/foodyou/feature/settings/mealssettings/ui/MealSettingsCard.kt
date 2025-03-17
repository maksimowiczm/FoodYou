package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealSettingsCard(viewModel: MealSettingsCardViewModel, modifier: Modifier = Modifier) {
    val meal by viewModel.meal.collectAsStateWithLifecycle()
    val state = rememberMealSettingsCardState(meal)

    MealSettingsCard(
        state = state,
        onDelete = {
            viewModel.deleteMeal(state.toMeal())
        },
        onUpdate = {
            viewModel.updateMeal(state.toMeal())
        },
        formatTime = viewModel::formatTime,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSettingsCard(
    state: MealSettingsCardState,
    onDelete: () -> Unit,
    onUpdate: () -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    showDeleteDialog: Boolean = true,
    shape: Shape = MealSettingsCardDefaults.shape,
    colors: MealSettingsCardColors = MealSettingsCardDefaults.colors()
) {
    val nameInput: @Composable RowScope.() -> Unit = {
        BasicTextField(
            value = state.nameInput.textFieldValue,
            onValueChange = { state.nameInput.onValueChange(it) },
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
            decorationBox = {
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
                            color = if (state.nameInput.error != null) {
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

    val actionButtonState by remember(state.isDirty) {
        derivedStateOf {
            when {
                state.nameInput.textFieldValue.text.isEmpty() -> ActionButtonState.Delete
                state.isDirty -> ActionButtonState.Save
                else -> ActionButtonState.Delete
            }
        }
    }

    var deleteDialog by rememberSaveable { mutableStateOf(false) }
    if (deleteDialog) {
        DeleteDialog(
            onDismissRequest = { deleteDialog = false },
            onConfirm = onDelete
        )
    }

    val actionButton = @Composable {
        when (actionButtonState) {
            ActionButtonState.Save -> FilledIconButton(
                onClick = onUpdate,
                enabled = state.isValid,
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
                onClick = {
                    if (showDeleteDialog) {
                        deleteDialog = true
                    } else {
                        onDelete()
                    }
                },
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
            if (state.isDirty) {
                colors.dirtyTimeContainerColor
            } else {
                colors.timeContainerColor
            }
        )

        val contentColor by animateColorAsState(
            if (state.isDirty) {
                colors.dirtyTimeContentColor
            } else {
                colors.timeContentColor
            }
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
                    text = formatTime(state.fromInput.value),
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
                    text = formatTime(state.toInput.value),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    if (showFromTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.fromInput.value.hour,
            initialMinute = state.fromInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showFromTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromTimePicker = false
                        state.fromInput.onValueChange(
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
            initialHour = state.toInput.value.hour,
            initialMinute = state.toInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showToTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToTimePicker = false
                        state.toInput.onValueChange(
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
        targetValue = if (state.isDirty) {
            colors.dirtyContainerColor
        } else {
            colors.containerColor
        }
    )

    val contentColor by animateColorAsState(
        targetValue = if (state.isDirty) {
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
                    actionButton()
                }

                AnimatedVisibility(
                    visible = state.nameInput.error != null
                ) {
                    Text(
                        text = state.nameInput.error?.stringResource() ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                AnimatedVisibility(
                    visible = !state.isAllDay
                ) {
                    Column(
                        modifier = Modifier.testTag(MealSettingsCardTestTags.TIME_PICKER)
                    ) {
                        Spacer(Modifier.height(8.dp))

                        dateInput()
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .testTag(MealSettingsCardTestTags.ALL_DAY_SWITCH_CONTAINER)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { state.setIsAllDay(!state.isAllDay) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = state.isAllDay,
                        onCheckedChange = state::setIsAllDay,
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

private enum class ActionButtonState {
    Save,
    Delete
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

data class MealSettingsCardColors(
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

object MealSettingsCardDefaults {
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
    ) = MealSettingsCardColors(
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
