package com.maksimowiczm.foodyou.features.home.ui.calendar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.extension.observeDate
import com.maksimowiczm.foodyou.shared.ui.extension.confirm
import com.maksimowiczm.foodyou.shared.ui.extension.horizontal
import com.maksimowiczm.foodyou.shared.ui.extension.minus
import com.maksimowiczm.foodyou.shared.ui.extension.now
import com.maksimowiczm.foodyou.shared.ui.extension.plus
import com.maksimowiczm.foodyou.shared.ui.extension.segmentFrequentTick
import com.maksimowiczm.foodyou.shared.ui.extension.vertical
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarCard(
    date: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val dateFlow = remember { Clock.System.observeDate() }
    val today by dateFlow.collectAsStateWithLifecycle(LocalDate.now())

    val state = rememberCalendarState(date, today)

    CalendarCard(
        state = state,
        onSelectDate = onSelectDate,
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@Composable
private fun CalendarCard(
    state: CalendarCardState,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val formatter = LocalDateFormatter.current
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val showDateDialog = rememberSaveable { mutableStateOf(false) }

    // Tick when user scrolls
    LaunchedEffect(state.listState) {
        combine(
                snapshotFlow { state.listState.isScrollInProgress },
                snapshotFlow { state.listState.firstVisibleItemIndex },
            ) { isScrollInProgress, _ ->
                if (isScrollInProgress) hapticFeedback.segmentFrequentTick()
            }
            .launchIn(this)
    }

    if (showDateDialog.value) {
        CalendarCardDatePickerDialog(
            state = state,
            referenceDate = state.referenceDate,
            onDismissRequest = { showDateDialog.value = false },
            onSelectDate = {
                onSelectDate(it)
                hapticFeedback.confirm()
                scope.launch { state.animateScrollTo(it) }
                showDateDialog.value = false
            },
        )
    }

    Column(
        modifier = modifier.padding(contentPadding.vertical()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = formatter.formatMonthYear(state.firstVisibleDate.value),
            style = MaterialTheme.typography.headlineMedium,
            modifier =
                Modifier.padding(contentPadding.horizontal())
                    .clickable(
                        onClick = { showDateDialog.value = true },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClickLabel = stringResource(Res.string.action_choose_other_date),
                    ),
        )
        LazyRow(
            modifier = Modifier.height(72.dp),
            state = state.listState,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(Int.MAX_VALUE) { day ->
                val date = remember(day) { CalendarCardState.zero + day.days }
                val selected = date == state.selectedDate
                val height =
                    animateDpAsState(
                        if (selected) 72.dp else 60.dp,
                        MaterialTheme.motionScheme.fastSpatialSpec(),
                    )
                val containerColor =
                    if (date == state.referenceDate) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surfaceContainer
                val colors =
                    ToggleButtonDefaults.toggleButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColorFor(containerColor),
                        checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                val padding =
                    animateIntAsState(
                        if (selected) 28 else 12,
                        MaterialTheme.motionScheme.fastEffectsSpec(),
                    )
                ToggleButton(
                    checked = selected,
                    onCheckedChange = {
                        onSelectDate(date)
                        hapticFeedback.confirm()
                    },
                    modifier = Modifier.height(height.value).widthIn(min = height.value),
                    shapes = ToggleButtonDefaults.shapes(MaterialTheme.shapes.medium, CircleShape),
                    colors = colors,
                    contentPadding = PaddingValues(horizontal = padding.value.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.labelLarge
                        ) {
                            val text =
                                remember(date, formatter) {
                                    buildString {
                                        appendLine(date.day)
                                        append(formatter.weekDayNamesShort[date.dayOfWeek.ordinal])
                                    }
                                }
                            Text(text = text, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCardDatePickerDialog(
    state: CalendarCardState,
    referenceDate: LocalDate,
    onDismissRequest: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    val zero = remember { Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.UTC).date }
    val last = remember { zero + Int.MAX_VALUE.days }
    val yearRange = remember { zero.year..last.year }

    val selectedDate = state.selectedDate
    val initialSelectedDateMillis =
        selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 } ?: 0

    val referenceDateVisible = state.referenceDateVisible(referenceDate)

    // If selected date is visible, we want to display it,
    // otherwise we want to display reference date if it's visible.
    // If none of them are visible, we want to display the first visible date.
    val initialDisplayedMonthMillis =
        when {
            state.selectedDateVisible.value -> initialSelectedDateMillis
            referenceDateVisible.value ->
                referenceDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 }
                    ?: 0

            else ->
                state.firstVisibleDate.value
                    .atStartOfDayIn(TimeZone.UTC)
                    .toEpochMilliseconds()
                    .takeIf { it >= 0 } ?: 0
        }

    val pickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
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
                        onSelectDate(date)
                    }
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text(text = stringResource(Res.string.positive_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, shapes = ButtonDefaults.shapes()) {
                Text(text = stringResource(Res.string.action_cancel))
            }
        },
    ) {
        DatePicker(
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
                        onClick = { onSelectDate(state.referenceDate) },
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

@Preview
@Composable
private fun CalendarCardPreview() {
    val state =
        rememberCalendarState(
            selectedDate = LocalDate.now(),
            referenceDate = LocalDate.now() - 2.days,
        )

    PreviewFoodYouTheme { CalendarCard(state = state, onSelectDate = {}) }
}
