package com.maksimowiczm.foodyou.feature.diary.database.converter

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

@Suppress("unused")
class WeightMeasurementTypeConverter {
    @TypeConverter
    fun fromWeightMeasurementType(weightMeasurementEnum: WeightMeasurementEnum) =
        when (weightMeasurementEnum) {
            WeightMeasurementEnum.WeightUnit -> 0
            WeightMeasurementEnum.Package -> 1
            WeightMeasurementEnum.Serving -> 2
        }

    @TypeConverter
    fun toWeightMeasurementType(weightMeasurementType: Int) = when (weightMeasurementType) {
        0 -> WeightMeasurementEnum.WeightUnit
        1 -> WeightMeasurementEnum.Package
        2 -> WeightMeasurementEnum.Serving
        else -> throw IllegalArgumentException("WeightMeasurementType not found")
    }
}

object WeightMeasurementSqlConstants {
    const val WEIGHT_UNIT = 0
    const val PACKAGE = 1
    const val SERVING = 2
}
