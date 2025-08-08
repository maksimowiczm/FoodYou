package com.maksimowiczm.foodyou.feature.goals.ui

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.date.plus
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

/**
 * The maximum number of pages allowed in the pager. This limit is set to 50,000 to ensure
 * reasonable performance and memory usage, while allowing users to select dates far into the past
 * and future.
 */
private const val MAX_PAGER_SIZE = 50_000

@Composable
internal fun rememberGoalsScreenState(selectedDate: LocalDate): GoalsScreenState {
    val maxSize = MAX_PAGER_SIZE

    val zeroDate = LocalDate.fromEpochDays(0)
    val initialPage = (zeroDate.until(selectedDate, DateTimeUnit.DAY)).toInt()

    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { maxSize })
    val coroutineScope = rememberCoroutineScope()

    return remember(pagerState, coroutineScope, selectedDate) {
        GoalsScreenState(
            coroutineScope = coroutineScope,
            zeroDate = zeroDate,
            pagerState = pagerState,
        )
    }
}

internal class GoalsScreenState(
    private val coroutineScope: CoroutineScope,
    private val zeroDate: LocalDate,
    val pagerState: PagerState,
) {
    val selectedDate by derivedStateOf { zeroDate.plus((pagerState.currentPage).days) }

    fun goToToday() {
        goToDate(LocalDate.now())
    }

    fun goToDate(date: LocalDate) {
        val page = (zeroDate.until(date, DateTimeUnit.DAY)).toInt()
        coroutineScope.launch { pagerState.animateScrollToPage(page) }
    }

    fun dateForPage(page: Int) = zeroDate.plus(page.days)

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
    @Composable
    fun rememberDatePickerState(): DatePickerState {
        val lastDate = zeroDate + (pagerState.pageCount - 1).days
        val yearRange = zeroDate.year..lastDate.year

        val initialSelectedDateMillis =
            selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 } ?: 0

        val initialDisplayedMonthMillis =
            selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds().takeIf { it >= 0 } ?: 0

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
