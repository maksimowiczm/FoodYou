package com.maksimowiczm.foodyou.app.ui.home.shared

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.common.extension.now
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.datetime.LocalDate

@Composable
internal fun rememberHomeState(initialSelectedDate: LocalDate = LocalDate.now()): HomeState {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    return rememberSaveable(
        saver =
            Saver(
                save = { it.selectedDate.toEpochDays() },
                restore = {
                    HomeState(initialSelectedDate = LocalDate.fromEpochDays(it), shimmer = shimmer)
                },
            )
    ) {
        HomeState(initialSelectedDate = initialSelectedDate, shimmer = shimmer)
    }
}

@Stable
internal class HomeState(initialSelectedDate: LocalDate, val shimmer: Shimmer) {
    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    fun selectDate(date: LocalDate) {
        selectedDate = date
    }
}
