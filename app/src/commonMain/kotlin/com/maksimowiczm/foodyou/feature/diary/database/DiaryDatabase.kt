package com.maksimowiczm.foodyou.feature.diary.database

import com.maksimowiczm.foodyou.feature.diary.database.meal.MealDao
import com.maksimowiczm.foodyou.feature.diary.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.diary.database.product.ProductDao

internal interface DiaryDatabase {
    val mealDao: MealDao
    val productDao: ProductDao
    val openFoodFactsDao: OpenFoodFactsDao
}
