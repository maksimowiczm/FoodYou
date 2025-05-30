package com.maksimowiczm.foodyou.core.domain.mapper

import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.domain.model.Measurement

object MeasurementMapper {
    @JvmName("toMeasurementFromEntityWithMeasurement")
    fun toMeasurement(entityWithMeasurement: EntityWithMeasurement): Measurement =
        entityWithMeasurement.toMeasurement()

    fun EntityWithMeasurement.toMeasurement(): Measurement = when (measurement) {
        MeasurementEntity.Gram -> Measurement.Gram(quantity)
        MeasurementEntity.Package -> Measurement.Package(quantity)
        MeasurementEntity.Serving -> Measurement.Serving(quantity)
    }

    @JvmName("toEntityFromMeasurement")
    fun toEntity(measurement: Measurement): MeasurementEntity = measurement.toEntity()

    fun Measurement.toEntity(): MeasurementEntity = when (this) {
        is Measurement.Gram -> MeasurementEntity.Gram
        is Measurement.Package -> MeasurementEntity.Package
        is Measurement.Serving -> MeasurementEntity.Serving
    }
}
