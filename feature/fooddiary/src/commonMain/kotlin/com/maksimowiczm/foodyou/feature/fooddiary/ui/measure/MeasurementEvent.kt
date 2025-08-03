package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

internal sealed interface MeasurementEvent {

    data object Deleted : MeasurementEvent
    data object Saved : MeasurementEvent
}
