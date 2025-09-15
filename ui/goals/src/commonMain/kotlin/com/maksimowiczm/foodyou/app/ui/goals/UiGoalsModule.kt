package com.maksimowiczm.foodyou.app.ui.goals

import com.maksimowiczm.foodyou.app.ui.goals.master.GoalsViewModel
import com.maksimowiczm.foodyou.app.ui.goals.setup.DailyGoalsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiGoalsModule = module {
    viewModelOf(::GoalsViewModel)
    viewModelOf(::DailyGoalsViewModel)
}
