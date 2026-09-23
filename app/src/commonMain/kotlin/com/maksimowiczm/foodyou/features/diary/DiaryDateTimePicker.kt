package com.maksimowiczm.foodyou.features.diary

import androidx.collection.intListOf
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker as MaterialDatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorPosition
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker as MaterialTimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.extension.confirm
import com.maksimowiczm.foodyou.shared.ui.extension.now
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utility.LocalUIFeatureFlags
import foodyou.app.generated.resources.*
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiaryDateTimePicker(
    meals: List<Meal>,
    selectedMeal: Meal,
    selectedDateTime: LocalDateTime,
    onMealChange: (Meal) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showTimestamps = LocalUIFeatureFlags.current.foodDiaryEntryTimestamps

    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MealPicker(
            selectedMeal = selectedMeal,
            meals = meals,
            onMealChange = onMealChange,
            modifier = Modifier.weight(if (showTimestamps) 2f else 1f).fillMaxHeight(),
        )
        DatePicker(
            date = selectedDateTime.date,
            onDateChange = onDateChange,
            hasTimePicker = showTimestamps,
            modifier = Modifier.weight(if (showTimestamps) 2f else 1f).fillMaxHeight(),
        )
        if (showTimestamps) {
            TimePicker(
                time = selectedDateTime.time,
                onTimeChange = onTimeChange,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun MealPicker(
    selectedMeal: Meal,
    meals: List<Meal>,
    onMealChange: (Meal) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val shape =
        rememberInteractionAnimatedShape(
            shapes =
                InteractionShapes(
                    shape =
                        MaterialTheme.shapes.extraSmall.copy(
                            topStart = MaterialTheme.shapes.large.topStart,
                            bottomStart = MaterialTheme.shapes.large.bottomStart,
                        ),
                    pressedShape = MaterialTheme.shapes.large,
                ),
            interactionSource = interactionSource,
        )
    Surface(
        onClick = { expanded = !expanded },
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        interactionSource = interactionSource,
    ) {
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            popupPositionProvider =
                MenuDefaults.rememberDropdownMenuPopupPositionProvider(
                    remember {
                        MenuAnchorPosition.Custom(
                            xCandidates = {
                                val centeredX =
                                    anchorBounds.left + (anchorBounds.width - menuSize.width) / 2
                                intListOf(centeredX)
                            },
                            yCandidates = { intListOf(anchorBounds.bottom) },
                        )
                    }
                ),
        ) {
            DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, meals.size)) {
                meals.forEachIndexed { i, meal ->
                    DropdownMenuItem(
                        selected = meal == selectedMeal,
                        onClick = {
                            expanded = false
                            onMealChange(meal)
                        },
                        text = { Text(meal.name) },
                        shapes = MenuDefaults.itemShape(i, meals.size),
                        horizontalArrangement = Arrangement.Center,
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = selectedMeal.name,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 2.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DatePicker(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    hasTimePicker: Boolean,
    modifier: Modifier = Modifier,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val dateFormatter = LocalDateFormatter.current
    val currentYear = remember { LocalDate.now().year }
    val interactionSource = remember { MutableInteractionSource() }
    val shape =
        rememberInteractionAnimatedShape(
            shapes =
                InteractionShapes(
                    shape =
                        if (hasTimePicker) MaterialTheme.shapes.extraSmall
                        else
                            MaterialTheme.shapes.extraSmall.copy(
                                topEnd = MaterialTheme.shapes.large.topEnd,
                                bottomEnd = MaterialTheme.shapes.large.bottomEnd,
                            ),
                    pressedShape = MaterialTheme.shapes.large,
                ),
            interactionSource = interactionSource,
        )

    if (showDialog) {
        DiaryDatePickerDialog(
            selectedDate = date,
            onDismissRequest = { showDialog = false },
            onSelectDate = {
                onDateChange(it)
                showDialog = false
            },
        )
    }

    Surface(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    if (date.year == currentYear) dateFormatter.formatDayMonth(date)
                    else dateFormatter.formatDateShort(date),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 2.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DiaryDatePickerDialog(
    selectedDate: LocalDate,
    onDismissRequest: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val today = remember { LocalDate.now() }
    val zero = remember { Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.UTC).date }
    val last = remember { LocalDate(2100, 12, 31) }
    val yearRange = remember { zero.year..last.year }

    val initialSelectedDateMillis =
        selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 } ?: 0

    val pickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialSelectedDateMillis,
            yearRange = yearRange,
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date =
                            Instant.fromEpochMilliseconds(utcTimeMillis)
                                .toLocalDateTime(TimeZone.UTC)
                                .date
                        return date in zero..last
                    }

                    override fun isSelectableYear(year: Int) = year in yearRange
                },
        )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let {
                        val date =
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                        hapticFeedback.confirm()
                        onSelectDate(date)
                    }
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text(stringResource(Res.string.positive_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
    ) {
        MaterialDatePicker(
            state = pickerState,
            title = {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DatePickerDefaults.DatePickerTitle(pickerState.displayMode)

                    TextButton(
                        onClick = {
                            hapticFeedback.confirm()
                            onSelectDate(today)
                        },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(stringResource(Res.string.action_go_to_today))
                    }
                }
            },
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun TimePicker(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val dateFormatter = LocalDateFormatter.current
    val interactionSource = remember { MutableInteractionSource() }
    val shape =
        rememberInteractionAnimatedShape(
            shapes =
                InteractionShapes(
                    shape =
                        MaterialTheme.shapes.extraSmall.copy(
                            topEnd = MaterialTheme.shapes.large.topEnd,
                            bottomEnd = MaterialTheme.shapes.large.bottomEnd,
                        ),
                    pressedShape = MaterialTheme.shapes.large,
                ),
            interactionSource = interactionSource,
        )

    if (showDialog) {
        DiaryTimePickerDialog(
            time = time,
            onDismissRequest = { showDialog = false },
            onSelectTime = {
                onTimeChange(it)
                showDialog = false
            },
        )
    }

    Surface(
        onClick = { showDialog = true },
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dateFormatter.formatTime(time),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 2.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DiaryTimePickerDialog(
    time: LocalTime,
    onDismissRequest: () -> Unit,
    onSelectTime: (LocalTime) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val pickerState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)

    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    hapticFeedback.confirm()
                    onSelectTime(
                        LocalTime(
                            hour = pickerState.hour,
                            minute = pickerState.minute,
                        )
                    )
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text(text = stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        title = {},
    ) {
        MaterialTimePicker(pickerState)
    }
}
