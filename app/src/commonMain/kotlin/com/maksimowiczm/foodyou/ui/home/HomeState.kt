package com.maksimowiczm.foodyou.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun rememberHomeState(
    initialSelectedDate: LocalDate = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).date
): HomeState {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window
    )

    return rememberSaveable(
        saver = Saver(
            save = {
                it.selectedDate.toEpochDays()
            },
            restore = {
                HomeState(
                    initialSelectedDate = LocalDate.fromEpochDays(it),
                    shimmer = shimmer
                )
            }
        )
    ) {
        HomeState(
            initialSelectedDate = initialSelectedDate,
            shimmer = shimmer
        )
    }
}

@Stable
class HomeState(initialSelectedDate: LocalDate, val shimmer: Shimmer) {
    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    fun selectDate(date: LocalDate) {
        selectedDate = date
    }
}
