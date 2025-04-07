package com.maksimowiczm.foodyou.feature.diary.core.database

import com.maksimowiczm.foodyou.feature.diary.core.database.meal.MealDao
import com.maksimowiczm.foodyou.feature.diary.core.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.feature.diary.core.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.diary.core.database.product.ProductDao
import com.maksimowiczm.foodyou.feature.diary.core.database.search.SearchDao

interface DiaryDatabase {
    val mealDao: MealDao
    val productDao: ProductDao
    val openFoodFactsDao: OpenFoodFactsDao
    val searchDao: SearchDao
    val measurementDao: MeasurementDao
}
