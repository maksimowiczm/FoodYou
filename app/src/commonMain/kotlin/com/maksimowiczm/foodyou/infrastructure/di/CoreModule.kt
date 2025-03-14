package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.DateProvider
import com.maksimowiczm.foodyou.data.DateProviderImpl
import com.maksimowiczm.foodyou.feature.home.calendarcard.ui.CalendarViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.meal.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.CreateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement.UpdateMeasurementViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.create.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.update.UpdateProductViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.card.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.GoalsSettingsViewModel
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.MealsSettingsViewModel
import com.maksimowiczm.foodyou.ui.DiaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    // -- Data
    single { DateProviderImpl() }.bind<DateProvider>()

    // -- Shared
    viewModelOf(::DiaryViewModel)

    // -- Calendar Card
    viewModelOf(::CalendarViewModel)

    // -- Goals Settings
    viewModelOf(::GoalsSettingsViewModel)

    // -- Meals Settings
    viewModelOf(::MealsSettingsViewModel)

    // -- Meals Card
    viewModelOf(::SearchViewModel)
    viewModelOf(::CreateMeasurementViewModel)
    viewModelOf(::UpdateMeasurementViewModel)
    viewModelOf(::MealsCardViewModel)
    viewModelOf(::DiaryDayMealViewModel)
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
}
