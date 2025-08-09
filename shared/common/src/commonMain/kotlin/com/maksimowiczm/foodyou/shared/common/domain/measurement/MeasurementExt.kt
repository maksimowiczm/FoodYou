package com.maksimowiczm.foodyou.shared.common.domain.measurement

val Measurement.type: MeasurementType
    get() =
        when (this) {
            is Measurement.Gram -> MeasurementType.Gram
            is Measurement.Milliliter -> MeasurementType.Milliliter
            is Measurement.Package -> MeasurementType.Package
            is Measurement.Serving -> MeasurementType.Serving
        }

val Measurement.rawValue: Double
    get() =
        when (this) {
            is Measurement.Gram -> this.value
            is Measurement.Milliliter -> this.value
            is Measurement.Package -> this.quantity
            is Measurement.Serving -> this.quantity
        }

fun Measurement.Companion.from(type: MeasurementType, rawValue: Double): Measurement =
    when (type) {
        MeasurementType.Gram -> Measurement.Gram(rawValue)
        MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
        MeasurementType.Package -> Measurement.Package(rawValue)
        MeasurementType.Serving -> Measurement.Serving(rawValue)
    }
