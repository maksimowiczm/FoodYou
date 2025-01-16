package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
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

@Composable
fun rememberDiaryState(
    zeroDay: LocalDate = LocalDate.ofEpochDay(0),
    initialReferenceDate: LocalDate = LocalDate.now(),
    initialSelectedDate: LocalDate = LocalDate.now()
): DiaryState {
    val coroutineScope = rememberCoroutineScope()

    val weekPagerState = rememberPagerState(
        pageCount = { 50_000 },
        initialPage = ChronoUnit.WEEKS.between(zeroDay, initialSelectedDate).toInt()
    )

    return rememberSaveable(
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
                    weekPagerState = weekPagerState,
                    zeroDate = zeroDay,
                    initialSelectedDate = selectedDate,
                    initialReferenceDate = initialReferenceDate
                )
            }
        )
    ) {
        DiaryState(
            coroutineScope = coroutineScope,
            weekPagerState = weekPagerState,
            zeroDate = zeroDay,
            initialSelectedDate = initialSelectedDate,
            initialReferenceDate = initialReferenceDate
        )
    }
}

class DiaryState(
    val coroutineScope: CoroutineScope,
    val weekPagerState: PagerState,
    val zeroDate: LocalDate,
    initialSelectedDate: LocalDate = LocalDate.now(),
    initialReferenceDate: LocalDate = LocalDate.now()
) {
    val referenceDate: LocalDate = initialReferenceDate
    val referenceWeekPage: Int = ChronoUnit.WEEKS.between(zeroDate, referenceDate).toInt()

    val referenceDateSelected by derivedStateOf {
        selectedDate != referenceDate
    }

    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    val targetWeek: LocalDate by derivedStateOf {
        zeroDate.plusWeeks(weekPagerState.targetPage.toLong())
    }

    private fun synchronizeWeekPager(date: LocalDate) {
        coroutineScope.launch {
            val weekPage = ChronoUnit.WEEKS.between(zeroDate, date).toInt()
            weekPagerState.animateScrollToPage(weekPage)
        }
    }

    fun onDateSelect(date: LocalDate) {
        selectedDate = date
        synchronizeWeekPager(date)
    }
}
