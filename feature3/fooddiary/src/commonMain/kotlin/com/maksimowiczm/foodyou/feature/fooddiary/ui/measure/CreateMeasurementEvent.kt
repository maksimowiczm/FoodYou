package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

internal sealed interface CreateMeasurementEvent {

    data object Deleted : CreateMeasurementEvent
    data object Saved : CreateMeasurementEvent
}
