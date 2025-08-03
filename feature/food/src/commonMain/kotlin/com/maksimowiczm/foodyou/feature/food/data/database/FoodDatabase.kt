package com.maksimowiczm.foodyou.feature.food.data.database

import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEventDao
import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductDao
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeDao
import com.maksimowiczm.foodyou.feature.food.data.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearchDao
import com.maksimowiczm.foodyou.feature.food.data.database.usda.USDAPagingKeyDao

interface FoodDatabase {
    val productDao: ProductDao
    val recipeDao: RecipeDao
    val foodSearchDao: FoodSearchDao
    val openFoodFactsDao: OpenFoodFactsDao
    val usdaPagingKeyDao: USDAPagingKeyDao
    val foodEventDao: FoodEventDao
}
