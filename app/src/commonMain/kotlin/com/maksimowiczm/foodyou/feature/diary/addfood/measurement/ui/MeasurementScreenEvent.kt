package com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui

internal sealed interface MeasurementScreenEvent {
    data object Closed : MeasurementScreenEvent
    data object FoodDeleted : MeasurementScreenEvent
}
