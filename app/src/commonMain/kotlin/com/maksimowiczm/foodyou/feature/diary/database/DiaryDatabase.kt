package com.maksimowiczm.foodyou.feature.diary.database

import com.maksimowiczm.foodyou.feature.diary.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.feature.diary.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.diary.database.dao.ProductDao

interface DiaryDatabase {
    fun addFoodDao(): AddFoodDao
    fun productDao(): ProductDao
    fun openFoodFactsDao(): OpenFoodFactsDao
}
