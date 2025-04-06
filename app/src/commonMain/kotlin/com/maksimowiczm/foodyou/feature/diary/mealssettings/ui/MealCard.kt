package com.maksimowiczm.foodyou.feature.diary.mealssettings.ui

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MealCard(
    state: MealCardState,
    formatTime: (LocalTime) -> String,
    onSave: () -> Unit,
    shouldShowDeleteDialog: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    colors: MealCardColors = MealCardDefaults.colors(),
    shape: Shape = MealCardDefaults.shape,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    action: (@Composable () -> Unit)? = null
) {
    val nameInput: @Composable RowScope.() -> Unit = {
        BasicTextField(
            state = state.nameInput,
            modifier = Modifier
                .testTag(MealCardTestTags.NAME_INPUT)
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
                            color = if (state.nameInput.text.isEmpty()) {
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

    val actionButtonState by remember(state.nameInput, state.isDirty) {
        derivedStateOf {
            when {
                state.nameInput.text.isEmpty() -> ActionButtonState.Delete
                state.isDirty -> ActionButtonState.Save
                else -> ActionButtonState.Delete
            }
        }
    }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    val actionButton = @Composable {
        if (action != null) {
            action()
        } else {
            when (actionButtonState) {
                ActionButtonState.Save -> FilledIconButton(
                    onClick = { onSave() },
                    enabled = true,
                    modifier = Modifier.testTag(MealCardTestTags.CONFIRM_BUTTON),
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
                        if (shouldShowDeleteDialog) {
                            showDeleteDialog = true
                        } else {
                            onDelete()
                        }
                    },
                    modifier = Modifier.testTag(MealCardTestTags.DELETE_BUTTON),
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
                modifier = Modifier.testTag(MealCardTestTags.FROM_TIME_PICKER),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = formatTime(state.fromTimeInput.value),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = stringResource(Res.string.en_dash),
                style = MaterialTheme.typography.headlineSmall
            )
            Card(
                onClick = { showToTimePicker = true },
                modifier = Modifier.testTag(MealCardTestTags.TO_TIME_PICKER),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = formatTime(state.toTimeInput.value),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    if (showFromTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.fromTimeInput.value.hour,
            initialMinute = state.fromTimeInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showFromTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromTimePicker = false
                        state.fromTimeInput.onValueChange(
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
            initialHour = state.toTimeInput.value.hour,
            initialMinute = state.toTimeInput.value.minute
        )

        TimePickerDialog(
            onDismissRequest = { showToTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToTimePicker = false
                        state.toTimeInput.onValueChange(
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
        shape = shape,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation
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
                    visible = !state.isAllDay.value
                ) {
                    Column(
                        modifier = Modifier.testTag(MealCardTestTags.TIME_PICKER)
                    ) {
                        Spacer(Modifier.height(8.dp))

                        dateInput()
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .testTag(MealCardTestTags.ALL_DAY_SWITCH_CONTAINER)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { state.isAllDay.value = !state.isAllDay.value }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = state.isAllDay.value,
                        onCheckedChange = { state.isAllDay.value = it },
                        modifier = Modifier.testTag(MealCardTestTags.ALL_DAY_SWITCH)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = stringResource(Res.string.headline_all_day),
                        style = MaterialTheme.typography.bodyLarge
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

internal data class MealCardColors(
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

internal object MealCardDefaults {
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

internal object MealCardTestTags {
    const val NAME_INPUT = "Name input"
    const val DELETE_BUTTON = "Delete button"
    const val CONFIRM_BUTTON = "Confirm button"
    const val TIME_PICKER = "Time picker"
    const val ALL_DAY_SWITCH_CONTAINER = "All day switch container"
    const val ALL_DAY_SWITCH = "All day switch"
    const val FROM_TIME_PICKER = "From time picker"
    const val TO_TIME_PICKER = "To time picker"
}
