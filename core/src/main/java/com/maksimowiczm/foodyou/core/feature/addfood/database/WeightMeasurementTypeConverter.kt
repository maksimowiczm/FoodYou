package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum

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
