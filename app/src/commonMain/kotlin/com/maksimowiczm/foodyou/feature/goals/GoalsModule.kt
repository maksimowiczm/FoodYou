package com.maksimowiczm.foodyou.feature.goals

import com.maksimowiczm.foodyou.feature.goals.data.DiaryDayRepository
import com.maksimowiczm.foodyou.feature.goals.ui.card.CaloriesCardViewModel
import com.maksimowiczm.foodyou.feature.goals.ui.screen.CaloriesScreenViewModel
import com.maksimowiczm.foodyou.feature.goals.ui.settings.GoalsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val goalsModule = module {
    factoryOf(::DiaryDayRepository)

    viewModelOf(::GoalsSettingsViewModel)
    viewModelOf(::CaloriesCardViewModel)
    viewModelOf(::CaloriesScreenViewModel)
}
