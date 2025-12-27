package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter

internal class FoodSourceTypeConverter {

    @TypeConverter
    fun fromFoodSourceType(value: FoodSourceType): Int =
        when (value) {
            FoodSourceType.User -> FoodSourceTypeSQLConstants.USER
            FoodSourceType.OpenFoodFacts -> FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS
<<<<<<< Updated upstream
            FoodSourceType.USDA -> FoodSourceTypeSQLConstants.USDA
            FoodSourceType.SwissFoodCompositionDatabase ->
                FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE
=======
            FoodSourceType.TBCA -> FoodSourceTypeSQLConstants.TBCA
>>>>>>> Stashed changes
        }

    @TypeConverter
    fun toFoodSourceType(value: Int): FoodSourceType =
        when (value) {
            FoodSourceTypeSQLConstants.USER -> FoodSourceType.User
            FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS -> FoodSourceType.OpenFoodFacts
<<<<<<< Updated upstream
            FoodSourceTypeSQLConstants.USDA -> FoodSourceType.USDA
            FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE ->
                FoodSourceType.SwissFoodCompositionDatabase
=======
            FoodSourceTypeSQLConstants.TBCA -> FoodSourceType.TBCA
            // Legacy values for backwards compatibility (if old data exists in DB)
            FoodSourceTypeSQLConstants.USDA,
            FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE,
            FoodSourceTypeSQLConstants.TACO -> FoodSourceType.User // Convert old sources to User
>>>>>>> Stashed changes
            else -> error("Unknown food source type value: $value")
        }
}

internal object FoodSourceTypeSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
    const val USDA = 2
    const val SWISS_FOOD_COMPOSITION_DATABASE = 3
}
