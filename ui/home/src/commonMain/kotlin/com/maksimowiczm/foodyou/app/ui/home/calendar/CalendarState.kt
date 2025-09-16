package com.maksimowiczm.foodyou.app.ui.home.calendar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.shared.common.extension.now
import com.maksimowiczm.foodyou.shared.common.extension.plus
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

// 2106 seems reasonable for now
private const val DIARY_DAYS_COUNT = 50_000

@OptIn(ExperimentalTime::class)
@Composable
internal fun rememberCalendarState(
    namesOfDayOfWeek: List<String>,
    zeroDay: LocalDate = LocalDate.fromEpochDays(0),
    referenceDate: LocalDate = LocalDate.now(),
    selectedDate: LocalDate = referenceDate,
): CalendarState {
    val coroutineScope = rememberCoroutineScope()

    val lazyListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex =
                (zeroDay.until(selectedDate, DateTimeUnit.DAY) - 2).toInt()
        )

    return remember(namesOfDayOfWeek, zeroDay, referenceDate, selectedDate) {
        CalendarState(
            coroutineScope = coroutineScope,
            namesOfDayOfWeek = namesOfDayOfWeek,
            lazyListCount = DIARY_DAYS_COUNT,
            lazyListState = lazyListState,
            zeroDate = zeroDay,
            initialSelectedDate = selectedDate,
            initialReferenceDate = referenceDate,
        )
    }
}

@OptIn(ExperimentalTime::class)
@Stable
internal class CalendarState(
    private val coroutineScope: CoroutineScope,
    val namesOfDayOfWeek: List<String>,
    val lazyListCount: Int,
    val lazyListState: LazyListState,
    val zeroDate: LocalDate,
    initialSelectedDate: LocalDate = LocalDate.now(),
    initialReferenceDate: LocalDate = initialSelectedDate,
) {
    val referenceDate: LocalDate = initialReferenceDate
    private val referenceDateVisible
        get() =
            lazyListState.layoutInfo.visibleItemsInfo.any {
                zeroDate.plus(it.index.days) == referenceDate
            }

    val firstVisibleDate by derivedStateOf {
        lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.let {
            zeroDate.plus(it.index.days)
        }
    }

    private val selectedDateVisible
        get() =
            lazyListState.layoutInfo.visibleItemsInfo.any {
                zeroDate.plus(it.index.days) == selectedDate
            }

    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    fun onDateSelect(date: LocalDate, scroll: Boolean) {
        selectedDate = date

        if (scroll) {
            coroutineScope.launch {
                lazyListState.scrollToItem(
                    index = zeroDate.until(date, DateTimeUnit.DAY).toInt(),
                    scrollOffset = -lazyListState.layoutInfo.viewportEndOffset / 2,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun rememberDatePickerState(): DatePickerState {
        val lastDate = zeroDate.plus(lazyListCount.toLong() - 1, DateTimeUnit.DAY)
        val yearRange = zeroDate.year..lastDate.year

        val initialSelectedDateMillis =
            selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 } ?: 0

        // If selected date is visible, we want to display it,
        // otherwise we want to display reference date if it's visible.
        // If none of them are visible, we want to display the first visible date.
        val initialDisplayedMonthMillis =
            if (selectedDateVisible) {
                initialSelectedDateMillis
            } else {
                if (referenceDateVisible) {
                        referenceDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                    } else {
                        firstVisibleDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
                    }
                    ?.takeIf { it >= 0 } ?: 0
            }

        return androidx.compose.material3.rememberDatePickerState(
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
                        return date in zeroDate..lastDate
                    }

                    override fun isSelectableYear(year: Int) = year in yearRange
                },
        )
    }
}
