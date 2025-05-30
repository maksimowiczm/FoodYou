package com.maksimowiczm.foodyou.core.domain.mapper

import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.domain.model.Measurement

object MeasurementMapper {
    fun toMeasurement(entityWithMeasurement: EntityWithMeasurement) = with(entityWithMeasurement) {
        when (measurement) {
            MeasurementEntity.Gram -> Measurement.Gram(quantity)
            MeasurementEntity.Package -> Measurement.Package(quantity)
            MeasurementEntity.Serving -> Measurement.Serving(quantity)
        }
    }

    fun toEntity(measurement: Measurement): MeasurementEntity = when (measurement) {
        is Measurement.Gram -> MeasurementEntity.Gram
        is Measurement.Package -> MeasurementEntity.Package
        is Measurement.Serving -> MeasurementEntity.Serving
    }

    fun toQuantity(measurement: Measurement): Float = when (measurement) {
        is Measurement.Gram -> measurement.value
        is Measurement.Package -> measurement.quantity
        is Measurement.Serving -> measurement.quantity
    }
}
