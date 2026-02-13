package com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe

import androidx.room.TypeConverter

internal class FoodReferenceTypeConverter {
    @TypeConverter
    fun fromFoodReferenceType(value: FoodReferenceType): Int =
        when (value) {
            FoodReferenceType.UserFood -> USER_PRODUCT
            FoodReferenceType.FoodDataCentral -> FOOD_DATA_CENTRAL
            FoodReferenceType.OpenFoodFacts -> OPEN_FOOD_FACTS
            FoodReferenceType.UserRecipe -> USER_RECIPE
        }

    @TypeConverter
    fun toFoodReferenceType(value: Int): FoodReferenceType =
        when (value) {
            USER_PRODUCT -> FoodReferenceType.UserFood
            FOOD_DATA_CENTRAL -> FoodReferenceType.FoodDataCentral
            OPEN_FOOD_FACTS -> FoodReferenceType.OpenFoodFacts
            USER_RECIPE -> FoodReferenceType.UserRecipe
            else -> error("Unknown food reference type: $value")
        }

    private companion object {
        private const val USER_PRODUCT = 0
        private const val FOOD_DATA_CENTRAL = 1
        private const val OPEN_FOOD_FACTS = 2
        private const val USER_RECIPE = 3
    }
}
