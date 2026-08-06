package com.maksimowiczm.foodyou.features.meal

sealed interface MealScheduleEvent {
    data object Updated : MealScheduleEvent
}
