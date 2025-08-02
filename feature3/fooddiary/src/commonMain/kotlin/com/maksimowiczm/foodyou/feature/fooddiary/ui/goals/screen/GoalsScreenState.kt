package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.core.ext.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.days

@Composable
internal fun rememberGoalsScreenState(
    zeroDate: LocalDate,
): GoalsScreenState {
    val maxSize = 50_000
    val pagerState = rememberPagerState(
        initialPage = maxSize / 2,
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

    val zeroDateSelected by derivedStateOf {
        selectedDate == zeroDate
    }

    fun goToZeroDate() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(initialPage)
        }
    }
}