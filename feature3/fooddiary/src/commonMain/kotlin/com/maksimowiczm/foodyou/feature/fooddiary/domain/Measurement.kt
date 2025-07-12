package com.maksimowiczm.foodyou.feature.fooddiary.domain

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
