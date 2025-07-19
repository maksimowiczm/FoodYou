package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.data.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.from

fun MeasurementSuggestion.toMeasurement(): Measurement = Measurement.from(measurement, quantity)

fun MeasurementEntity.toMeasurement(): Measurement = Measurement.from(measurement, quantity)
