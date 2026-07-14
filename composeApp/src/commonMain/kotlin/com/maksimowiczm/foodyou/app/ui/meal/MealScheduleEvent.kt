package com.maksimowiczm.foodyou.app.ui.meal

sealed interface MealScheduleEvent {
    data object Updated : MealScheduleEvent
}
