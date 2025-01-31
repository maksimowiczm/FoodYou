package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// 2106 seems reasonable for now
private const val DIARY_DAYS_COUNT = 50_000
private const val MILLIS_IN_DAY = 86400000

@Composable
fun rememberDiaryState(
    namesOfDayOfWeek: Array<String>,
    zeroDay: LocalDate = LocalDate.ofEpochDay(0),
    initialReferenceDate: LocalDate = LocalDate.now(),
    initialSelectedDate: LocalDate = LocalDate.now()
): DiaryState {
    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex =
        ChronoUnit.DAYS.between(zeroDay, initialSelectedDate).toInt() - 2
    )

    return rememberSaveable(
        namesOfDayOfWeek,
        zeroDay,
        initialReferenceDate,
        initialSelectedDate,
        saver = Saver(
            save = {
                arrayOf(
                    it.selectedDate.toEpochDay()
                )
            },
            restore = {
                val selectedDate = LocalDate.ofEpochDay(it[0])

                DiaryState(
                    coroutineScope = coroutineScope,
                    namesOfDayOfWeek = namesOfDayOfWeek,
                    lazyListCount = DIARY_DAYS_COUNT,
                    lazyListState = lazyListState,
                    zeroDate = zeroDay,
                    initialSelectedDate = selectedDate,
                    initialReferenceDate = initialReferenceDate
                )
            }
        )
    ) {
        DiaryState(
            coroutineScope = coroutineScope,
            namesOfDayOfWeek = namesOfDayOfWeek,
            lazyListCount = DIARY_DAYS_COUNT,
            lazyListState = lazyListState,
            zeroDate = zeroDay,
            initialSelectedDate = initialSelectedDate,
            initialReferenceDate = initialReferenceDate
        )
    }
}

@Stable
class DiaryState(
    private val coroutineScope: CoroutineScope,
    val namesOfDayOfWeek: Array<String>,
    val lazyListCount: Int,
    val lazyListState: LazyListState,
    val zeroDate: LocalDate,
    initialSelectedDate: LocalDate = LocalDate.now(),
    initialReferenceDate: LocalDate = LocalDate.now()
) {
    val referenceDate: LocalDate = initialReferenceDate
    private val referenceDateVisible
        get() = lazyListState.layoutInfo.visibleItemsInfo.any {
            zeroDate.plusDays(it.index.toLong()) == referenceDate
        }

    val firstVisibleDate by derivedStateOf {
        lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.let {
            zeroDate.plusDays(it.index.toLong())
        }
    }

    var selectedDate by mutableStateOf(initialSelectedDate)
        private set
    private val selectedDateVisible
        get() = lazyListState.layoutInfo.visibleItemsInfo.any {
            zeroDate.plusDays(it.index.toLong()) == selectedDate
        }

    fun onDateSelect(date: LocalDate, scroll: Boolean) {
        selectedDate = date

        if (scroll) {
            coroutineScope.launch {
                lazyListState.scrollToItem(
                    index = ChronoUnit.DAYS.between(zeroDate, date).toInt(),
                    scrollOffset = -lazyListState.layoutInfo.viewportEndOffset / 2
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun rememberDatePickerState(): DatePickerState {
        val lastDate = zeroDate.plusDays(lazyListCount.toLong() - 1)
        val yearRange = zeroDate.year..lastDate.year

        // If selected date is visible, we want to display it,
        // otherwise we want to display reference date if it's visible.
        // If none of them are visible, we want to display the first visible date.
        val initialDisplayedMonthMillis = if (selectedDateVisible) {
            selectedDate.toEpochDay() * MILLIS_IN_DAY
        } else if (referenceDateVisible) {
            referenceDate.toEpochDay() * MILLIS_IN_DAY
        } else {
            firstVisibleDate?.toEpochDay()?.let { it * MILLIS_IN_DAY }
        }

        return androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * MILLIS_IN_DAY,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = LocalDate.ofEpochDay(utcTimeMillis / MILLIS_IN_DAY)
                    return date in zeroDate..lastDate
                }

                override fun isSelectableYear(year: Int) = year in yearRange
            }
        )
    }
}
