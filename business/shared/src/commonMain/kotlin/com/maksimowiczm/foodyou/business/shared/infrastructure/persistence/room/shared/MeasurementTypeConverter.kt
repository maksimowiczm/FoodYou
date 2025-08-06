package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared

import androidx.room.TypeConverter

internal class MeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(measurement: MeasurementType) =
        when (measurement) {
            MeasurementType.Gram -> MeasurementTypeSQLConstants.GRAM
            MeasurementType.Milliliter -> MeasurementTypeSQLConstants.MILLILITER
            MeasurementType.Package -> MeasurementTypeSQLConstants.PACKAGE
            MeasurementType.Serving -> MeasurementTypeSQLConstants.SERVING
        }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) =
        when (weightMeasurementType) {
            MeasurementTypeSQLConstants.GRAM -> MeasurementType.Gram
            MeasurementTypeSQLConstants.MILLILITER -> MeasurementType.Milliliter
            MeasurementTypeSQLConstants.PACKAGE -> MeasurementType.Package
            MeasurementTypeSQLConstants.SERVING -> MeasurementType.Serving
            else -> error("WeightMeasurementType not found")
        }
}

internal object MeasurementTypeSQLConstants {
    const val GRAM = 0
    const val PACKAGE = 1
    const val SERVING = 2
    const val MILLILITER = 3
}
