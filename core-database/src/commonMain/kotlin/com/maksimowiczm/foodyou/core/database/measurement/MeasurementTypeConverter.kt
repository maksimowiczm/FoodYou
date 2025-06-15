package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.TypeConverter

@Suppress("unused")
class MeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(measurement: Measurement) = when (measurement) {
        Measurement.Gram -> MeasurementSQLConstants.GRAM
        Measurement.Milliliter -> MeasurementSQLConstants.MILLILITER
        Measurement.Package -> MeasurementSQLConstants.PACKAGE
        Measurement.Serving -> MeasurementSQLConstants.SERVING
    }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) = when (weightMeasurementType) {
        MeasurementSQLConstants.GRAM -> Measurement.Gram
        MeasurementSQLConstants.MILLILITER -> Measurement.Milliliter
        MeasurementSQLConstants.PACKAGE -> Measurement.Package
        MeasurementSQLConstants.SERVING -> Measurement.Serving
        else -> throw IllegalArgumentException("WeightMeasurementType not found")
    }
}

object MeasurementSQLConstants {
    const val GRAM = 0
    const val PACKAGE = 1
    const val SERVING = 2
    const val MILLILITER = 3
}
