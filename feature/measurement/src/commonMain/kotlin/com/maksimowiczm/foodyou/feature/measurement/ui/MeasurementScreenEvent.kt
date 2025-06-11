package com.maksimowiczm.foodyou.feature.measurement.ui

internal sealed interface MeasurementScreenEvent {
    /**
     * Work here is done and the screen should be closed.
     */
    data object Done : MeasurementScreenEvent

    /**
     * The food was deleted.
     */
    data object FoodDeleted : MeasurementScreenEvent
}
