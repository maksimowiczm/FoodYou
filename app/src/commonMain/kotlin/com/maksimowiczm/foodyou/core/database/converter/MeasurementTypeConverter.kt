package com.maksimowiczm.foodyou.core.database.converter

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.core.database.entity.Measurement

@Suppress("unused")
class MeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(measurement: Measurement) = when (measurement) {
        Measurement.Gram -> MeasurementSQLConstants.GRAM
        Measurement.Package -> MeasurementSQLConstants.PACKAGE
        Measurement.Serving -> MeasurementSQLConstants.SERVING
    }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) = when (weightMeasurementType) {
        MeasurementSQLConstants.GRAM -> Measurement.Gram
        MeasurementSQLConstants.PACKAGE -> Measurement.Package
        MeasurementSQLConstants.SERVING -> Measurement.Serving
        else -> throw IllegalArgumentException("WeightMeasurementType not found")
    }
}

object MeasurementSQLConstants {
    const val GRAM = 0
    const val PACKAGE = 1
    const val SERVING = 2
}
