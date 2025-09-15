package com.maksimowiczm.foodyou.feature.food.diary.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.common.extension.minus
import com.maksimowiczm.foodyou.shared.common.extension.now
import com.maksimowiczm.foodyou.shared.common.extension.plus
import com.maksimowiczm.foodyou.shared.compose.utility.LocalDateFormatter
import foodyou.app.generated.resources.*
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun ChipsDatePicker(state: ChipsDatePickerState, modifier: Modifier = Modifier) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis
                            ?.let(Instant::fromEpochMilliseconds)
                            ?.toLocalDateTime(TimeZone.UTC)
                            ?.date
                            ?.let(state::addAndSelect)

                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.positive_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(modifier) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
        }

        Spacer(Modifier.width(8.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.dates.forEach { date ->
                InputChip(
                    selected = state.selectedDate == date,
                    onClick = { state.selectDate(date) },
                    label = { Text(date.stringResource(state.today)) },
                )
            }

            InputChip(
                selected = false,
                onClick = { showDatePicker = true },
                label = { Text(stringResource(Res.string.action_choose_other_date)) },
            )
        }
    }
}

@Composable
private fun LocalDate.stringResource(today: LocalDate): String {
    val formatter = LocalDateFormatter.current
    val str = formatter.formatDateShort(this)

    return when (this) {
        today -> stringResource(Res.string.headline_today, str)
        today.minus(1.days) -> stringResource(Res.string.headline_yesterday, str)
        today.plus(1.days) -> stringResource(Res.string.headline_tomorrow, str)
        else -> str
    }
}

@Composable
fun rememberChipsDatePickerState(
    today: LocalDate,
    initialDates: List<LocalDate>,
    selectedDate: LocalDate = initialDates.first(),
): ChipsDatePickerState =
    rememberSaveable(today, initialDates, selectedDate, saver = ChipsDatePickerState.saver) {
        ChipsDatePickerState(
            today = today,
            initialDates = initialDates,
            initialSelectedDate = selectedDate,
        )
    }

@Stable
class ChipsDatePickerState(
    val today: LocalDate,
    initialDates: List<LocalDate>,
    initialSelectedDate: LocalDate?,
) {
    private var datesSet by mutableStateOf(initialDates.ifEmpty { setOf(LocalDate.now()) })

    val dates by derivedStateOf { datesSet.sorted() }

    var selectedDate by mutableStateOf(initialSelectedDate ?: LocalDate.now())
        private set

    fun addAndSelect(date: LocalDate) {
        datesSet = (datesSet + date)

        selectedDate = date
    }

    fun selectDate(date: LocalDate) {
        if (date in datesSet) {
            selectedDate = date
        }
    }

    companion object {
        val saver: Saver<ChipsDatePickerState, List<Long>> =
            Saver(
                save = {
                    val mutableList = mutableListOf<Long>()

                    mutableList.add(it.today.toEpochDays())
                    mutableList.add(it.selectedDate.toEpochDays())
                    mutableList.addAll(it.datesSet.map(LocalDate::toEpochDays))

                    mutableList
                },
                restore = {
                    val selectedDate = it.firstOrNull()?.let(LocalDate::fromEpochDays)
                    val today = it.getOrNull(1)?.let(LocalDate::fromEpochDays) ?: LocalDate.now()
                    val dates = it.drop(2).map(LocalDate::fromEpochDays)

                    ChipsDatePickerState(
                        today = today,
                        initialDates = dates,
                        initialSelectedDate = selectedDate,
                    )
                },
            )
    }
}
