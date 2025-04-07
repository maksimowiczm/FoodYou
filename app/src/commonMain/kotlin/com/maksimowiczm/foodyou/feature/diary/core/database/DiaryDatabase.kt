package com.maksimowiczm.foodyou.feature.diary.core.database

import com.maksimowiczm.foodyou.feature.diary.core.database.meal.MealDao
import com.maksimowiczm.foodyou.feature.diary.core.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.diary.core.database.product.ProductDao

internal interface DiaryDatabase {
    val mealDao: MealDao
    val productDao: ProductDao
    val openFoodFactsDao: OpenFoodFactsDao
}
