package com.maksimowiczm.foodyou.food.infrastructure.room

interface FoodDatabase {
    val productDao: ProductDao
    val recipeDao: RecipeDao
    val foodEventDao: FoodEventDao
    val measurementSuggestionDao: MeasurementSuggestionDao
}
