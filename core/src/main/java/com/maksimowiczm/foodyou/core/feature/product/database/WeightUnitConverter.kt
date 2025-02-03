package com.maksimowiczm.foodyou.core.feature.product.database

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit

@Suppress("unused")
class WeightUnitConverter {
    @TypeConverter
    fun fromWeightUnit(weightUnit: WeightUnit): Int = when (weightUnit) {
        WeightUnit.Gram -> 0
        WeightUnit.Milliliter -> 1
    }

    @TypeConverter
    fun toWeightUnit(weightUnit: Int): WeightUnit = when (weightUnit) {
        0 -> WeightUnit.Gram
        1 -> WeightUnit.Milliliter
        else -> throw IllegalArgumentException("WeightUnit not found")
    }
}
