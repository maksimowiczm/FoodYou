package com.maksimowiczm.foodyou.app.ui.goals.setup

internal sealed interface DailyGoalsViewModelEvent {

    data object Updated : DailyGoalsViewModelEvent
}
