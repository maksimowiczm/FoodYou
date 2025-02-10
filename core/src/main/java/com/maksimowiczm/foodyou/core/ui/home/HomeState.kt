package com.maksimowiczm.foodyou.core.ui.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun rememberHomeState(
    initialSelectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
): HomeState {
    return rememberSaveable(
        saver = Saver(
            save = {
                it.selectedDate.toEpochDays()
            },
            restore = {
                HomeState(
                    initialSelectedDate = LocalDate.fromEpochDays(it)
                )
            }
        )
    ) {
        HomeState(
            initialSelectedDate = initialSelectedDate
        )
    }
}

@Stable
class HomeState(
    initialSelectedDate: LocalDate
) : com.maksimowiczm.foodyou.core.feature.HomeState {
    override var selectedDate by mutableStateOf(initialSelectedDate)
        private set

    override fun selectDate(date: LocalDate) {
        Log.d("HomeState", "selectDate: $date")

        selectedDate = date
    }
}
