package com.maksimowiczm.foodyou.feature.food.data

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource

class SourceTypeConverter {
    @TypeConverter
    fun fromSourceType(sourceType: FoodSource.Type): Int = when (sourceType) {
        FoodSource.Type.User -> SourceSQLConstants.USER
        FoodSource.Type.OpenFoodFacts -> SourceSQLConstants.OPEN_FOOD_FACTS
    }

    @TypeConverter
    fun toSourceType(sourceType: Int): FoodSource.Type = when (sourceType) {
        SourceSQLConstants.USER -> FoodSource.Type.User
        SourceSQLConstants.OPEN_FOOD_FACTS -> FoodSource.Type.OpenFoodFacts
        else -> error("SourceType not found")
    }
}

object SourceSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
}
