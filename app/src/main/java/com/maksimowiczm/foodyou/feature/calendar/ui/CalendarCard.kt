package com.maksimowiczm.foodyou.feature.calendar.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun CalendarCard(
    calendarState: CalendarState,
    formatMonthYear: (LocalDate) -> String,
    modifier: Modifier = Modifier,
    colors: CalendarCardColors = CalendarCardDefaults.colors()
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        CalendarCardDatePickerDialog(
            calendarState = calendarState,
            onDismissRequest = { showDatePicker = false }
        )
    }

    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Icon button adds enough padding top
                .padding(bottom = 8.dp, top = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Icon button adds enough padding end
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMonthYear(
                        calendarState.firstVisibleDate ?: calendarState.selectedDate
                    )
                )

                IconButton(
                    onClick = {
                        showDatePicker = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar_month_24),
                        contentDescription = "Calendar"
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                CalendarCardDatePicker(
                    calendarState = calendarState,
                    colors = colors
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarCardDatePickerDialog(
    calendarState: CalendarState,
    onDismissRequest: () -> Unit
) {
    val state = calendarState.rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let {
                        calendarState.onDateSelect(
                            date = Instant
                                .fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date,
                            scroll = true
                        )
                    }
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(R.string.positive_ok)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = stringResource(R.string.action_cancel)
                )
            }
        }
    ) {
        DatePicker(
            state = state,
            // It won't fit on small screens, so we need to scroll
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun CalendarCardDatePicker(
    calendarState: CalendarState,
    colors: CalendarCardColors,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    // Tick when user scrolls
    LaunchedEffect(calendarState) {
        combine(
            snapshotFlow { calendarState.lazyListState.isScrollInProgress },
            snapshotFlow { calendarState.lazyListState.firstVisibleItemIndex }
        ) { isScrollInProgress, _ ->
            isScrollInProgress
        }.filter { it }.collectLatest {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    LazyRow(
        modifier = modifier,
        state = calendarState.lazyListState
    ) {
        items(calendarState.lazyListCount) {
            val date = calendarState.zeroDate.plus(it.toLong(), DateTimeUnit.DAY)
            DatePickerRowItem(
                calendarState = calendarState,
                date = date,
                colors = colors,
                onClick = {
                    calendarState.onDateSelect(
                        date = date,
                        scroll = false
                    )
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            )
        }
    }
}

@Composable
private fun DatePickerRowItem(
    calendarState: CalendarState,
    date: LocalDate,
    colors: CalendarCardColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val namesOfDayOfWeek = calendarState.namesOfDayOfWeek
    val referenceDate = calendarState.referenceDate
    val selectedDate = calendarState.selectedDate
    val dayOfWeek = (date.dayOfWeek.value - 1) % 7

    val backgroundColor by animateColorAsState(
        targetValue = when (date) {
            selectedDate -> colors.selectedDateContainerColor
            referenceDate -> colors.referenceDateContainerColor
            else -> colors.containerColor
        },
        animationSpec = tween(500),
        label = "Date background color"
    )
    val color by animateColorAsState(
        targetValue = when (date) {
            selectedDate -> colors.selectedDateContentColor
            referenceDate -> colors.referenceDateContentColor
            else -> colors.contentColor
        },
        animationSpec = tween(500),
        label = "Date text color"
    )

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(4.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .drawBehind { drawRect(backgroundColor) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.aspectRatio(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = namesOfDayOfWeek[dayOfWeek],
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                textAlign = TextAlign.Center
            )
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Immutable
data class CalendarCardColors(
    val containerColor: Color,
    val contentColor: Color,
    val selectedDateContainerColor: Color,
    val selectedDateContentColor: Color,
    val referenceDateContainerColor: Color,
    val referenceDateContentColor: Color
)

object CalendarCardDefaults {
    @Composable
    fun colors(
        containerColor: Color = CardDefaults.elevatedCardColors().containerColor,
        contentColor: Color = CardDefaults.elevatedCardColors().contentColor,
        selectedDateContainerColor: Color = MaterialTheme.colorScheme.primary,
        selectedDateContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        referenceDateContainerColor: Color = MaterialTheme.colorScheme.secondary,
        referenceDateContentColor: Color = MaterialTheme.colorScheme.onSecondary
    ) = CalendarCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        selectedDateContainerColor = selectedDateContainerColor,
        selectedDateContentColor = selectedDateContentColor,
        referenceDateContainerColor = referenceDateContainerColor,
        referenceDateContentColor = referenceDateContentColor
    )
}

@Preview
@Composable
private fun CalendarCardPreview() {
    val namesOfDayOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val referenceDate = LocalDate(2024, 12, 17)
    val selectedDate = LocalDate(2024, 12, 18)
    val formatMonthYear = { _: LocalDate -> "December 2024" }

    FoodYouTheme {
        CalendarCard(
            calendarState = rememberCalendarState(
                namesOfDayOfWeek = namesOfDayOfWeek,
                zeroDay = LocalDate.fromEpochDays(4),
                referenceDate = referenceDate,
                selectedDate = selectedDate
            ),
            formatMonthYear = formatMonthYear
        )
    }
}
