package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

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
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.ext.plus
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun rememberGoalsScreenState(zeroDate: LocalDate): GoalsScreenState {
    val maxSize = 50_000
    val pagerState = rememberPagerState(
        initialPage = maxSize / 2
    ) { maxSize }
    val coroutineScope = rememberCoroutineScope()

    return remember {
        GoalsScreenState(
            coroutineScope = coroutineScope,
            initialPage = maxSize / 2,
            zeroDate = zeroDate,
            pagerState = pagerState
        )
    }
}

internal class GoalsScreenState(
    private val coroutineScope: CoroutineScope,
    private val initialPage: Int,
    private val zeroDate: LocalDate,
    val pagerState: PagerState
) {
    val selectedDate by derivedStateOf {
        zeroDate.plus((pagerState.currentPage - initialPage).days)
    }

    fun goToToday() {
        goToDate(LocalDate.now())
    }

    fun goToDate(date: LocalDate) {
        val page = (date - zeroDate).days + initialPage
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    fun dateForPage(page: Int) = zeroDate.plus((page - initialPage).days)

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
    @Composable
    fun rememberDatePickerState(): DatePickerState {
        val lastDate = zeroDate.plus((pagerState.pageCount - initialPage - 1).days)
        val yearRange = zeroDate.year..lastDate.year

        val initialSelectedDateMillis = selectedDate
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()
            .takeIf { it >= 0 } ?: 0

        val initialDisplayedMonthMillis = zeroDate
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()
            .takeIf { it >= 0 } ?: 0

        return androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant
                        .fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.UTC).date
                    return date in zeroDate..lastDate
                }

                override fun isSelectableYear(year: Int) = year in yearRange
            }
        )
    }
}
