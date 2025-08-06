package com.maksimowiczm.foodyou.feature.calendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.calendar.ui.CalendarCard
import com.maksimowiczm.foodyou.shared.ui.HomeState

@Composable
fun CalendarCard(homeState: HomeState, modifier: Modifier = Modifier) {
    CalendarCard(
        homeState = homeState,
        modifier = modifier
    )
}
