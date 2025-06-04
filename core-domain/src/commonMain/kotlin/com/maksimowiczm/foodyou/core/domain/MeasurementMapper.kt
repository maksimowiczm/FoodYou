package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.WithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement

interface MeasurementMapper {
    fun toMeasurement(entity: WithMeasurement): Measurement

    fun toEntity(measurement: Measurement): MeasurementEntity

    fun toQuantity(measurement: Measurement): Float
}

internal object MeasurementMapperImpl : MeasurementMapper {
    override fun toMeasurement(entity: WithMeasurement): Measurement = with(entity) {
        when (measurement) {
            MeasurementEntity.Gram -> Measurement.Gram(quantity)
            MeasurementEntity.Package -> Measurement.Package(quantity)
            MeasurementEntity.Serving -> Measurement.Serving(quantity)
        }
    }

    override fun toEntity(measurement: Measurement): MeasurementEntity = when (measurement) {
        is Measurement.Gram -> MeasurementEntity.Gram
        is Measurement.Package -> MeasurementEntity.Package
        is Measurement.Serving -> MeasurementEntity.Serving
    }

    override fun toQuantity(measurement: Measurement): Float = when (measurement) {
        is Measurement.Gram -> measurement.value
        is Measurement.Package -> measurement.quantity
        is Measurement.Serving -> measurement.quantity
    }
}
