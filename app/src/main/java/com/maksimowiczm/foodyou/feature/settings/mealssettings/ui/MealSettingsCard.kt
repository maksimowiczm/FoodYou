package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.stringParser
import com.maksimowiczm.foodyou.ui.preview.MealsPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.datetime.LocalTime

private enum class ActionButtonState {
    Save,
    Delete,
    Loading
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSettingsCard(
    state: MealSettingsCardState,
    showDeleteDialog: Boolean,
    onDelete: () -> Unit,
    onConfirm: () -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier,
    shape: Shape = MealSettingsCardDefaults.shape,
    colors: MealSettingsCardColors = MealSettingsCardDefaults.colors()
) {
    val nameInput: @Composable RowScope.() -> Unit = {
        BasicTextField(
            value = state.nameInput.textFieldValue,
            onValueChange = { state.nameInput.onValueChange(it) },
            enabled = !state.isLoading,
            modifier = Modifier
                .defaultMinSize(minWidth = 50.dp)
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

    val actionButtonState by remember(state.dirty, state.isLoading) {
        derivedStateOf {
            when {
                state.isLoading -> ActionButtonState.Loading
                state.nameInput.textFieldValue.text.isEmpty() -> ActionButtonState.Delete
                state.dirty -> ActionButtonState.Save
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
        AnimatedContent(
            targetState = actionButtonState
        ) {
            when (it) {
                ActionButtonState.Save -> FilledIconButton(
                    onClick = onConfirm,
                    enabled = state.isValid,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.confirmButtonContainerColor,
                        contentColor = colors.confirmButtonContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.action_confirm)
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
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.deleteButtonContainerColor,
                        contentColor = colors.deleteButtonContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete)
                    )
                }

                ActionButtonState.Loading -> Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    var showFromTimePicker by rememberSaveable { mutableStateOf(false) }
    var showToTimePicker by rememberSaveable { mutableStateOf(false) }

    val dateInput = @Composable {
        val containerColor by animateColorAsState(
            if (state.isLoading) {
                colors.disabledTimeContainerColor
            } else if (state.dirty) {
                colors.dirtyTimeContainerColor
            } else {
                colors.timeContainerColor
            }
        )

        val contentColor by animateColorAsState(
            if (state.isLoading) {
                colors.disabledTimeContentColor
            } else if (state.dirty) {
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
                enabled = !state.isLoading,
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
                text = stringResource(R.string.en_dash),
                style = MaterialTheme.typography.headlineSmall
            )
            Card(
                onClick = { showToTimePicker = true },
                enabled = !state.isLoading,
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
                        text = stringResource(R.string.action_confirm)
                    )
                }
            },
            title = {},
            dismissButton = {
                TextButton(
                    onClick = { showFromTimePicker = false }
                ) {
                    Text(
                        text = stringResource(R.string.action_cancel)
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
                        text = stringResource(R.string.action_confirm)
                    )
                }
            },
            title = {},
            dismissButton = {
                TextButton(
                    onClick = { showToTimePicker = false }
                ) {
                    Text(
                        text = stringResource(R.string.action_cancel)
                    )
                }
            }

        ) {
            TimePicker(timePickerState)
        }
    }

    val surfaceColor by animateColorAsState(
        targetValue = if (state.isLoading) {
            colors.disabledContainerColor
        } else if (state.dirty) {
            colors.dirtyContainerColor
        } else {
            colors.containerColor
        }
    )

    val contentColor by animateColorAsState(
        targetValue = if (state.isLoading) {
            colors.disabledContentColor
        } else if (state.dirty) {
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

                Spacer(Modifier.height(8.dp))

                dateInput()
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
                    text = stringResource(R.string.action_delete)
                )
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = stringResource(R.string.action_cancel)
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
                text = stringResource(R.string.neutral_permanently_delete_meal)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.neutral_meal_will_be_deleted_permanently)
            )
        }
    )
}

data class MealSettingsCardColors(
    val containerColor: Color,
    val contentColor: Color,
    val timeContainerColor: Color,
    val timeContentColor: Color,

    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val disabledTimeContainerColor: Color,
    val disabledTimeContentColor: Color,

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
        disabledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        disabledTimeContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
        disabledTimeContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        disabledTimeContainerColor = disabledTimeContainerColor,
        disabledTimeContentColor = disabledTimeContentColor,
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

@Preview
@Composable
private fun MealSettingsCardPreview() {
    val meal = MealsPreviewParameterProvider().values.first()

    FoodYouTheme {
        MealSettingsCard(
            state = rememberMealsSettingsCardState(meal),
            onDelete = {},
            onConfirm = {},
            showDeleteDialog = true,
            formatTime = { it.toString() }
        )
    }
}

@PreviewDynamicColors
@Composable
private fun DirtyMealSettingsCardPreview() {
    val meal = MealsPreviewParameterProvider().values.first()

    FoodYouTheme {
        MealSettingsCard(
            state = rememberMealsSettingsCardState(
                initialName = meal.name,
                initialFrom = meal.from,
                initialTo = meal.to,
                nameInput = rememberFormFieldWithTextFieldValue(
                    initialTextFieldValue = TextFieldValue("I am dirty"),
                    initialValue = "I am dirty",
                    parser = stringParser(
                        onEmpty = { MealNameError.Empty }
                    )
                )
            ),
            onDelete = {},
            onConfirm = {},
            showDeleteDialog = true,
            formatTime = { it.toString() }
        )
    }
}
