package com.maksimowiczm.foodyou.feature.diary.core.database.measurement

import androidx.room.TypeConverter

@Suppress("unused")
class MeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(measurement: Measurement) = when (measurement) {
        Measurement.Gram -> 0
        Measurement.Package -> 1
        Measurement.Serving -> 2
    }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) = when (weightMeasurementType) {
        0 -> Measurement.Gram
        1 -> Measurement.Package
        2 -> Measurement.Serving
        else -> throw IllegalArgumentException("WeightMeasurementType not found")
    }
}
