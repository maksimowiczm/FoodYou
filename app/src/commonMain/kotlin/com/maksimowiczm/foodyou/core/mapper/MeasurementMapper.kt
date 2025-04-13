package com.maksimowiczm.foodyou.core.mapper

import com.maksimowiczm.foodyou.core.database.core.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.model.Measurement

object MeasurementMapper {
    fun EntityWithMeasurement.toMeasurement(): Measurement = when (measurement) {
        MeasurementEntity.Gram -> Measurement.Gram(quantity)
        MeasurementEntity.Package -> Measurement.Package(quantity)
        MeasurementEntity.Serving -> Measurement.Serving(quantity)
    }

    fun Measurement.toEntity(): MeasurementEntity = when (this) {
        is Measurement.Gram -> MeasurementEntity.Gram
        is Measurement.Package -> MeasurementEntity.Package
        is Measurement.Serving -> MeasurementEntity.Serving
    }
}
