package com.maksimowiczm.foodyou.feature.home.calendarcard

import androidx.navigation.NavController
import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.home.HomeFeature
import com.maksimowiczm.foodyou.feature.home.calendarcard.ui.CalendarCard
import com.maksimowiczm.foodyou.feature.home.calendarcard.ui.CalendarViewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * CalendarFeature is HomeFeature that displays a calendar card.
 */
class CalendarCard(
    private val stringFormatRepository: Module.() -> KoinDefinition<StringFormatRepository>,
    private val dateProvider: Module.() -> KoinDefinition<DateProvider>
) : Feature.Home {
    override fun KoinApplication.module() = module {
        viewModelOf(::CalendarViewModel)

        stringFormatRepository()
        dateProvider()
    }

    override fun build(navController: NavController) = HomeFeature { _, modifier, homeState ->
        CalendarCard(
            homeState = homeState,
            modifier = modifier
        )
    }
}
