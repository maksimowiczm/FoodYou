package com.maksimowiczm.foodyou.app.ui.home.calendar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.extension.now
import com.maksimowiczm.foodyou.app.ui.common.extension.plus
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

@Composable
fun rememberCalendarCardState(
    referenceDate: LocalDate = LocalDate.now(),
    selectedDate: LocalDate = LocalDate.now(),
): CalendarCardState {
    val zeroDay = LocalDate.fromEpochDays(0)

    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex =
                (zeroDay.until(selectedDate, DateTimeUnit.DAY) - 2).toInt()
        )

    val selectedEpochState = remember(selectedDate) { mutableStateOf(selectedDate.toEpochDays()) }

    return remember(listState, selectedEpochState, referenceDate) {
        CalendarCardState(listState, selectedEpochState, referenceDate)
    }
}

@Stable
class CalendarCardState(
    val listState: LazyListState,
    selectedEpochState: MutableState<Long>,
    val referenceDate: LocalDate,
    val daysCount: Int = DIARY_DAYS_COUNT,
) {
    private var selectedEpoch by selectedEpochState

    val selectedDate by derivedStateOf { LocalDate.fromEpochDays(selectedEpoch) }

    val firstVisibleDate by derivedStateOf {
        val firstVisibleIndex = listState.firstVisibleItemIndex
        LocalDate.fromEpochDays(firstVisibleIndex)
    }

    val referenceDateVisible by derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.any { zeroDate.plus(it.index.days) == referenceDate }
    }

    val selectedDateVisible by derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.any { zeroDate.plus(it.index.days) == selectedDate }
    }

    /** Selects the given [date]. */
    fun selectDate(date: LocalDate) {
        selectedEpoch = date.toEpochDays()
    }

    private fun LocalDate.index(): Int = toEpochDays().toInt()

    suspend fun animateScrollTo(date: LocalDate) {
        listState.animateScrollToItem(
            index = date.index(),
            scrollOffset = -listState.layoutInfo.viewportEndOffset / 2,
        )
    }

    suspend fun snapScrollTo(date: LocalDate) {
        listState.scrollToItem(
            index = date.index(),
            scrollOffset = -listState.layoutInfo.viewportEndOffset / 2,
        )
    }

    @Composable
    fun rememberDatePickerState(): DatePickerState {
        val lastDate = zeroDate.plus(daysCount.toLong() - 1, DateTimeUnit.DAY)
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
                        firstVisibleDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                    }
                    .takeIf { it >= 0 } ?: 0
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

    companion object {
        // 2106 seems reasonable for now
        private const val DIARY_DAYS_COUNT = 50_000
        private val zeroDate: LocalDate = LocalDate.fromEpochDays(0)
    }
}
