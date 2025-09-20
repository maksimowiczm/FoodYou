package com.maksimowiczm.foodyou.app.ui.goals.setup2

internal sealed interface DailyGoalsViewModelEvent {

    data object Updated : DailyGoalsViewModelEvent
}
