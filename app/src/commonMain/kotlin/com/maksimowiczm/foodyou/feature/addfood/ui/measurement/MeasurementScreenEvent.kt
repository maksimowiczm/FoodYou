package com.maksimowiczm.foodyou.feature.addfood.ui.measurement

internal sealed interface MeasurementScreenEvent {
    data object Closed : MeasurementScreenEvent
    data object FoodDeleted : MeasurementScreenEvent
}
