package com.maksimowiczm.foodyou.feature.food.data.database

import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodDao
import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductDao
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeDao
import com.maksimowiczm.foodyou.feature.food.data.database.openfoodfacts.OpenFoodFactsDao

interface FoodDatabase {
    val productDao: ProductDao
    val recipeDao: RecipeDao
    val foodDao: FoodDao
    val openFoodFactsDao: OpenFoodFactsDao
}
