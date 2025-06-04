package com.maksimowiczm.foodyou.core.database.food

interface FoodDatabase {
    val productDao: ProductLocalDataSource
    val recipeDao: RecipeLocalDataSource
}
