package com.maksimowiczm.foodyou.app.infrastructure.room.shared

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.shared.measurement.MeasurementType

internal class MeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(measurement: MeasurementType) =
        when (measurement) {
            MeasurementType.Gram -> MeasurementTypeSQLConstants.GRAM
            MeasurementType.Milliliter -> MeasurementTypeSQLConstants.MILLILITER
            MeasurementType.Package -> MeasurementTypeSQLConstants.PACKAGE
            MeasurementType.Serving -> MeasurementTypeSQLConstants.SERVING
            MeasurementType.Ounce -> MeasurementTypeSQLConstants.OUNCE
            MeasurementType.FluidOunce -> MeasurementTypeSQLConstants.FLUID_OUNCE
        }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) =
        when (weightMeasurementType) {
            MeasurementTypeSQLConstants.GRAM -> MeasurementType.Gram
            MeasurementTypeSQLConstants.MILLILITER -> MeasurementType.Milliliter
            MeasurementTypeSQLConstants.PACKAGE -> MeasurementType.Package
            MeasurementTypeSQLConstants.SERVING -> MeasurementType.Serving
            MeasurementTypeSQLConstants.OUNCE -> MeasurementType.Ounce
            MeasurementTypeSQLConstants.FLUID_OUNCE -> MeasurementType.FluidOunce
            else -> error("WeightMeasurementType not found")
        }
}

internal object MeasurementTypeSQLConstants {
    const val GRAM = 0
    const val PACKAGE = 1
    const val SERVING = 2
    const val MILLILITER = 3
    const val OUNCE = 4
    const val FLUID_OUNCE = 5
}
