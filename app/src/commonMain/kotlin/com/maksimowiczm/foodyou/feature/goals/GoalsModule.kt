package com.maksimowiczm.foodyou.feature.goals

import com.maksimowiczm.foodyou.feature.goals.ui.GoalsSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val goalsModule = module {
    viewModelOf(::GoalsSettingsViewModel)
}
