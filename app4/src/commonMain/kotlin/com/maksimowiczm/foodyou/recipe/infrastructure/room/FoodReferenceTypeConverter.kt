package com.maksimowiczm.foodyou.recipe.infrastructure.room

import androidx.room.TypeConverter

internal class FoodReferenceTypeConverter {
    @TypeConverter
    fun fromFoodReferenceType(value: FoodReferenceType): Int =
        when (value) {
            FoodReferenceType.UserFood -> USER_FOOD
            FoodReferenceType.FoodDataCentral -> FOOD_DATA_CENTRAL
            FoodReferenceType.OpenFoodFacts -> OPEN_FOOD_FACTS
            FoodReferenceType.Recipe -> RECIPE
        }

    @TypeConverter
    fun toFoodReferenceType(value: Int): FoodReferenceType =
        when (value) {
            USER_FOOD -> FoodReferenceType.UserFood
            FOOD_DATA_CENTRAL -> FoodReferenceType.FoodDataCentral
            OPEN_FOOD_FACTS -> FoodReferenceType.OpenFoodFacts
            RECIPE -> FoodReferenceType.Recipe
            else -> error("Unknown food reference type: $value")
        }

    private companion object {
        private const val USER_FOOD = 0
        private const val FOOD_DATA_CENTRAL = 1
        private const val OPEN_FOOD_FACTS = 2
        private const val RECIPE = 3
    }
}
