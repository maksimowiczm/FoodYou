package com.maksimowiczm.foodyou.foodyou3.infrastructure

internal object FoodYou3FoodSourceTypeConverter {

    fun fromFoodSourceType(value: FoodYou3FoodSourceType): Int =
        when (value) {
            FoodYou3FoodSourceType.User -> FoodYou3FoodSourceTypeSQLConstants.USER
            FoodYou3FoodSourceType.OpenFoodFacts ->
                FoodYou3FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS
            FoodYou3FoodSourceType.USDA -> FoodYou3FoodSourceTypeSQLConstants.USDA
            FoodYou3FoodSourceType.SwissFoodCompositionDatabase ->
                FoodYou3FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE
        }

    fun toFoodSourceType(value: Int): FoodYou3FoodSourceType =
        when (value) {
            FoodYou3FoodSourceTypeSQLConstants.USER -> FoodYou3FoodSourceType.User
            FoodYou3FoodSourceTypeSQLConstants.OPEN_FOOD_FACTS ->
                FoodYou3FoodSourceType.OpenFoodFacts
            FoodYou3FoodSourceTypeSQLConstants.USDA -> FoodYou3FoodSourceType.USDA
            FoodYou3FoodSourceTypeSQLConstants.SWISS_FOOD_COMPOSITION_DATABASE ->
                FoodYou3FoodSourceType.SwissFoodCompositionDatabase
            else -> error("Unknown food source type value: $value")
        }
}

internal object FoodYou3FoodSourceTypeSQLConstants {
    const val USER = 0
    const val OPEN_FOOD_FACTS = 1
    const val USDA = 2
    const val SWISS_FOOD_COMPOSITION_DATABASE = 3
}
