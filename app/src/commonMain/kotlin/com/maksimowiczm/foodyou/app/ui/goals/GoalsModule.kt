package com.maksimowiczm.foodyou.app.ui.goals

import com.maksimowiczm.foodyou.app.ui.goals.master.GoalsViewModel
import com.maksimowiczm.foodyou.app.ui.goals.setup.DailyGoalsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

fun Module.goals() {
    viewModelOf(::GoalsViewModel)
    viewModelOf(::DailyGoalsViewModel)
}
