package com.maksimowiczm.foodyou.feature.food.data

interface FoodDatabase {
    val productDao: ProductDao
    val recipeDao: RecipeDao
    val foodDao: FoodDao
}
