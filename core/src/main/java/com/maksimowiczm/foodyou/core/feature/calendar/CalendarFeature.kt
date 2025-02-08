package com.maksimowiczm.foodyou.core.feature.calendar

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.HomeFeature
import com.maksimowiczm.foodyou.core.feature.calendar.ui.CalendarCard
import com.maksimowiczm.foodyou.core.feature.calendar.ui.CalendarViewModel
import com.maksimowiczm.foodyou.core.feature.calendar.ui.rememberCalendarState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val calendarModule = module {
    viewModelOf(::CalendarViewModel)
}

/**
 * CalendarFeature is HomeFeature that displays a calendar card.
 */
object CalendarFeature : Feature.Koin, Feature.Home {
    override fun buildHomeFeatures(navController: NavController) = listOf(CalendarCard)

    override fun KoinApplication.setup() {
        modules(calendarModule)
    }
}

private val CalendarCard = HomeFeature { modifier, homeState ->
    val viewModel = koinViewModel<CalendarViewModel>()

    val today by viewModel.today.collectAsStateWithLifecycle()

    val calendarState = rememberCalendarState(
        namesOfDayOfWeek = remember { viewModel.weekDayNamesShort },
        referenceDate = today,
        selectedDate = homeState.selectedDate
    )

    LaunchedEffect(calendarState.selectedDate) {
        homeState.selectDate(calendarState.selectedDate)
    }

    CalendarCard(
        calendarState = calendarState,
        formatMonthYear = viewModel::formatMonthYear,
        modifier = modifier
    )
}
