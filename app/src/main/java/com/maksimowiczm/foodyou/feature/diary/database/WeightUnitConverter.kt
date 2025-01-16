package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit

@Suppress("unused")
class WeightUnitConverter {
    @TypeConverter
    fun fromWeightUnit(weightUnit: WeightUnit): Int = when (weightUnit) {
        WeightUnit.Gram -> 0
        WeightUnit.Millilitre -> 1
    }

    @TypeConverter
    fun toWeightUnit(weightUnit: Int): WeightUnit = when (weightUnit) {
        0 -> WeightUnit.Gram
        1 -> WeightUnit.Millilitre
        else -> throw IllegalArgumentException("WeightUnit not found")
    }
}
