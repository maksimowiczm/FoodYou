package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.diary.goals.domain.GoalsRepository
import com.maksimowiczm.foodyou.feature.diary.goals.ui.card.GoalsCardViewModel
import com.maksimowiczm.foodyou.feature.diary.goals.ui.screen.CaloriesScreenViewModel
import com.maksimowiczm.foodyou.feature.diary.goals.ui.settings.GoalsSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val goalsModule = module {
    factoryOf(::GoalsRepository)

    viewModelOf(::GoalsSettingsViewModel)
    viewModelOf(::CaloriesScreenViewModel)
    viewModelOf(::GoalsCardViewModel)
}
