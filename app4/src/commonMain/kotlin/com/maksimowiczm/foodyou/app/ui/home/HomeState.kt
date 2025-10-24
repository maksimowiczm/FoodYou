package com.maksimowiczm.foodyou.app.ui.home

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.datetime.LocalDate

@Composable
fun rememberHomeState(navController: NavController, initialSelectedDate: LocalDate): HomeState {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    return rememberSaveable(
        shimmer,
        navController,
        saver =
            Saver(
                save = { it.selectedDate.toEpochDays() },
                restore = {
                    HomeState(
                        initialSelectedDate = LocalDate.fromEpochDays(it),
                        shimmer = shimmer,
                        navController = navController,
                    )
                },
            ),
    ) {
        HomeState(
            initialSelectedDate = initialSelectedDate,
            shimmer = shimmer,
            navController = navController,
        )
    }
}

@Stable
class HomeState(
    initialSelectedDate: LocalDate,
    val navController: NavController,
    val shimmer: Shimmer,
) {
    var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    fun selectDate(date: LocalDate) {
        selectedDate = date
    }
}
