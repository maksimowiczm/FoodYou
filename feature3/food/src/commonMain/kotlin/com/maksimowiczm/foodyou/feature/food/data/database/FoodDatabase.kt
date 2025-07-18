package com.maksimowiczm.foodyou.feature.food.data.database

import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductDao
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeDao
import com.maksimowiczm.foodyou.feature.food.data.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearchDao

interface FoodDatabase {
    val productDao: ProductDao
    val recipeDao: RecipeDao
    val foodSearchDao: FoodSearchDao
    val openFoodFactsDao: OpenFoodFactsDao
}
