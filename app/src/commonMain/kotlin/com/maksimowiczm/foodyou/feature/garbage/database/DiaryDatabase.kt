package com.maksimowiczm.foodyou.feature.garbage.database

import com.maksimowiczm.foodyou.feature.garbage.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.feature.garbage.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.garbage.database.dao.ProductDao

interface DiaryDatabase {
    fun addFoodDao(): AddFoodDao
    fun productDao(): ProductDao
    fun openFoodFactsDao(): OpenFoodFactsDao
}
