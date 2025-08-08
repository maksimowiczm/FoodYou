package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement

fun Measurement.toEntityType(): MeasurementType =
    when (this) {
        is Measurement.Gram -> MeasurementType.Gram
        is Measurement.Milliliter -> MeasurementType.Milliliter
        is Measurement.Package -> MeasurementType.Package
        is Measurement.Serving -> MeasurementType.Serving
    }

fun Measurement.toEntityValue(): Double =
    when (this) {
        is Measurement.Gram -> this.value
        is Measurement.Milliliter -> this.value
        is Measurement.Package -> this.quantity
        is Measurement.Serving -> this.quantity
    }

fun measurementFrom(type: MeasurementType, rawValue: Double): Measurement =
    when (type) {
        MeasurementType.Gram -> Measurement.Gram(rawValue)
        MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
        MeasurementType.Package -> Measurement.Package(rawValue)
        MeasurementType.Serving -> Measurement.Serving(rawValue)
    }
