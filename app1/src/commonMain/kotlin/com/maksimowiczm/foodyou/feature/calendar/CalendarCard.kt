package com.maksimowiczm.foodyou.feature.calendar

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.HomeFeature
import com.maksimowiczm.foodyou.feature.calendar.ui.CalendarCard
import com.maksimowiczm.foodyou.feature.calendar.ui.CalendarViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * CalendarFeature is HomeFeature that displays a calendar card.
 */
object CalendarCard : Feature.Home() {
    override fun build(navController: NavController) = HomeFeature { _, modifier, homeState ->
        CalendarCard(
            homeState = homeState,
            modifier = modifier
        )
    }

    override val module: Module = module {
        viewModelOf(::CalendarViewModel)
    }
}
