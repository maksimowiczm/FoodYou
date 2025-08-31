package com.maksimowiczm.foodyou.feature.goals.setup

internal sealed interface DailyGoalsViewModelEvent {

    data object Updated : DailyGoalsViewModelEvent
}
