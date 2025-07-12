package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.data.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

val Measurement.rawValue: Float
    get() = when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> quantity
        is Measurement.Serving -> quantity
    }

val Measurement.type: MeasurementType
    get() = when (this) {
        is Measurement.Gram -> MeasurementType.Gram
        is Measurement.Milliliter -> MeasurementType.Milliliter
        is Measurement.Package -> MeasurementType.Package
        is Measurement.Serving -> MeasurementType.Serving
    }

fun Measurement.Companion.from(type: MeasurementType, rawValue: Float): Measurement = when (type) {
    MeasurementType.Gram -> Measurement.Gram(rawValue)
    MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
    MeasurementType.Package -> Measurement.Package(rawValue)
    MeasurementType.Serving -> Measurement.Serving(rawValue)
}

fun MeasurementSuggestion.toMeasurement(): Measurement = Measurement.from(measurement, quantity)

fun MeasurementEntity.toMeasurement(): Measurement = Measurement.from(measurement, quantity)
