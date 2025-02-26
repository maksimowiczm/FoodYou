package com.maksimowiczm.foodyou.feature.home.calendarcard

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.home.calendarcard.ui.CalendarCard

/**
 * CalendarFeature is HomeFeature that displays a calendar card.
 */
object CalendarCard : Feature.Home {
    override fun build(navController: NavController) = HomeFeature { _, modifier, homeState ->
        CalendarCard(
            homeState = homeState,
            modifier = modifier
        )
    }
}
