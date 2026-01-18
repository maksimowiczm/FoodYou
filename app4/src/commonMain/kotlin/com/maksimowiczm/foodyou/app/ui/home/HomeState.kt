package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.datetime.LocalDate

@Composable
fun rememberHomeState(initialSelectedDate: LocalDate): HomeState {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    return rememberSaveable(
        shimmer,
        saver =
            Saver(
                save = { it.selectedDate.toEpochDays() },
                restore = {
                    HomeState(initialSelectedDate = LocalDate.fromEpochDays(it), shimmer = shimmer)
                },
            ),
    ) {
        HomeState(initialSelectedDate = initialSelectedDate, shimmer = shimmer)
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
