package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.utils.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MealCard(
    state: MealCardState,
    isDragging: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val dateFormatter = LocalDateFormatter.current

    val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
    val shadowElevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    val nameInput: @Composable RowScope.() -> Unit = {
        BasicTextField(
            state = state.name.textFieldState,
            modifier = Modifier
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
                            color = if (state.name.error != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null
                    )
                }
            }
        )
    }
    val iconButton: @Composable RowScope.() -> Unit = {
        when {
            action != null -> action()

            state.isModified -> {
                FilledIconButton(
                    onClick = {
                        if (state.isValid) {
                            onSave()
                        }
                    },
                    enabled = state.isValid
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(Res.string.action_save)
                    )
                }
            }

            !state.isModified -> {
                IconButton(
                    onClick = { showDeleteDialog = true }
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
    if (showFromTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.fromTime.hour,
            initialMinute = state.fromTime.minute
        )

        TimePickerDialog(
            onDismissRequest = { showFromTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromTimePicker = false
                        state.fromTime = LocalTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
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

    var showToTimePicker by rememberSaveable { mutableStateOf(false) }
    if (showToTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.toTime.hour,
            initialMinute = state.toTime.minute
        )

        TimePickerDialog(
            onDismissRequest = { showToTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToTimePicker = false
                        state.toTime = LocalTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
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

    val dateInput = @Composable {
        val containerColor by animateColorAsState(
            if (state.isModified) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        )

        val contentColor by animateColorAsState(
            if (state.isModified) {
                MaterialTheme.colorScheme.onTertiaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Card(
                onClick = { showFromTimePicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = dateFormatter.formatTime(state.fromTime),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = stringResource(Res.string.en_dash),
                style = MaterialTheme.typography.headlineSmall
            )
            Card(
                onClick = { showToTimePicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = dateFormatter.formatTime(state.toTime),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    val surfaceColor by animateColorAsState(
        targetValue = if (state.isModified) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }
    )

    val contentColor by animateColorAsState(
        targetValue = if (state.isModified) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    )

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = surfaceColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                nameInput()
                iconButton()
            }

            AnimatedVisibility(
                visible = !state.isAllDay
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))

                    dateInput()
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { state.isAllDay = !state.isAllDay }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = state.isAllDay,
                    onCheckedChange = null
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
