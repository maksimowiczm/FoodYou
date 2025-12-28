package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter

internal class FoodSourceTypeConverter {
    @TypeConverter
    fun fromFoodSourceType(value: FoodSourceType): Int =
        when (value) {
            FoodSourceType.User -> FoodSourceTypeSQLConstants.USER
            FoodSourceType.OpenFoodFacts -> FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS
            FoodSourceType.TBCA -> FoodSourceTypeSQLConstants.TBCA
        }

    @TypeConverter
    fun toFoodSourceType(value: Int): FoodSourceType =
        when (value) {
            FoodSourceTypeSQLConstants.USER -> FoodSourceType.User
            FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS -> FoodSourceType.OpenFoodFacts
            FoodSourceTypeSQLConstants.TBCA -> FoodSourceType.TBCA
            FoodSourceTypeSQLConstants.USDA,
            FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE,
            FoodSourceTypeSQLConstants.TACO -> FoodSourceType.User
        else -> error("Unknown food source type value: $value")
        }
}

internal object FoodSourceTypeSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
    const val USDA = 2
    const val SWISS_FOOD_COMPOSITION_DATABASE = 3
    const val TACO = 4
    const val TBCA = 5
}
