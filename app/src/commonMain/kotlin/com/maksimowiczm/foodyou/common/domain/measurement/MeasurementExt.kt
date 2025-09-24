package com.maksimowiczm.foodyou.common.domain.measurement

val Measurement.type: MeasurementType
    get() =
        when (this) {
            is Measurement.Gram -> MeasurementType.Gram
            is Measurement.Milliliter -> MeasurementType.Milliliter
            is Measurement.Ounce -> MeasurementType.Ounce
            is Measurement.FluidOunce -> MeasurementType.FluidOunce
            is Measurement.Package -> MeasurementType.Package
            is Measurement.Serving -> MeasurementType.Serving
        }

val Measurement.rawValue: Double
    get() =
        when (this) {
            is Measurement.Gram -> this.value
            is Measurement.Milliliter -> this.value
            is Measurement.Ounce -> this.value
            is Measurement.FluidOunce -> this.value
            is Measurement.Package -> this.quantity
            is Measurement.Serving -> this.quantity
        }

fun Measurement.Companion.from(type: MeasurementType, rawValue: Double): Measurement =
    when (type) {
        MeasurementType.Gram -> Measurement.Gram(rawValue)
        MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
        MeasurementType.Package -> Measurement.Package(rawValue)
        MeasurementType.Serving -> Measurement.Serving(rawValue)
        MeasurementType.Ounce -> Measurement.Ounce(rawValue)
        MeasurementType.FluidOunce -> Measurement.FluidOunce(rawValue)
    }
