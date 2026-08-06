package com.maksimowiczm.foodyou.features.home.calendar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

@Stable
internal class CalendarCardState(
    val listState: LazyListState,
    val selectedDate: LocalDate,
    val referenceDate: LocalDate,
) {
    val selectedDateIndex: Int = zero.until(selectedDate, DateTimeUnit.DAY).toInt()

    val firstVisibleDate: State<LocalDate> = derivedStateOf {
        LocalDate.fromEpochDays(listState.firstVisibleItemIndex)
    }

    val selectedDateVisible: State<Boolean> = derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.any { it.index == selectedDateIndex }
    }

    fun referenceDateVisible(referenceDate: LocalDate): State<Boolean> = derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.any {
            it.index == zero.until(referenceDate, DateTimeUnit.DAY).toInt()
        }
    }

    suspend fun animateScrollTo(date: LocalDate) {
        listState.animateScrollToItem(
            index = date.index(),
            scrollOffset = -listState.layoutInfo.viewportEndOffset / 2,
        )
    }

    companion object {
        val zero by
            lazy(LazyThreadSafetyMode.NONE) {
                Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.UTC).date
            }

        private fun LocalDate.index(): Int = toEpochDays().toInt()
    }
}

@Composable
internal fun rememberCalendarState(
    selectedDate: LocalDate,
    referenceDate: LocalDate,
): CalendarCardState {
    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex =
                CalendarCardState.zero.until(selectedDate, DateTimeUnit.DAY).toInt() - 3
        )

    return remember(listState, selectedDate, referenceDate) {
        CalendarCardState(listState, selectedDate, referenceDate)
    }
}
