package com.maksimowiczm.foodyou.feature.settings.goals.presentation

sealed interface DailyGoalsViewModelEvent {

    data object Updated : DailyGoalsViewModelEvent
}
