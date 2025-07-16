package com.maksimowiczm.foodyou.feature.food.data.database

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource

class SourceTypeConverter {
    @TypeConverter
    fun fromSourceType(sourceType: FoodSource.Type): Int = when (sourceType) {
        FoodSource.Type.User -> FoodSourceSQLConstants.USER
        FoodSource.Type.OpenFoodFacts -> FoodSourceSQLConstants.OPEN_FOOD_FACTS
    }

    @TypeConverter
    fun toSourceType(sourceType: Int): FoodSource.Type = when (sourceType) {
        FoodSourceSQLConstants.USER -> FoodSource.Type.User
        FoodSourceSQLConstants.OPEN_FOOD_FACTS -> FoodSource.Type.OpenFoodFacts
        else -> error("SourceType not found")
    }
}

object FoodSourceSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
}
